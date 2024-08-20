package com.daniel.gameguru.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.DbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_Guide extends AppCompatActivity {

    private MaterialTextView guideTitleTextView;
    private WebView guideContentWebView;
    private MaterialTextView gameNameLink, authorUsername;
    private String gameId;
    private FloatingActionButton fabEditGuide;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String authorName;
    private String authorId;
    private String guideId;
    private Guide guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//TODO: add search functionality
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        guideId = getIntent().getStringExtra("guideId");
        gameId = getIntent().getStringExtra("gameId");
        authorId = getIntent().getStringExtra("authorId");
        Log.d("Activity_Guide_GAMe", "Starting edit activity with guideId: " + authorId);

        db.collection("users").document(authorId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                authorName = documentSnapshot.getString("username");
                authorUsername.setText(authorName);
            } else {
                Toast.makeText(this, "Author not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        addGuideToUserRecent(guideId);
        findViews();
        initViews();
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadGuideData();

        fabEditGuide.setOnClickListener(v -> {
            Intent editIntent = new Intent(Activity_Guide.this, Activity_CreateGuide.class);
            editIntent.putExtra("guideId", guideId);
            startActivity(editIntent);
        });
    }

    private void initViews() {

        // Configure the WebView
        guideContentWebView.getSettings().setJavaScriptEnabled(true);
        guideContentWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (URLUtil.isValidUrl(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        gameNameLink.setOnClickListener(v -> {
            if(gameId == null) {
                Toast.makeText(this, "No game Exists", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Activity_Guide.this, Activity_Game.class);
            intent.putExtra("gameId", gameId); // Pass the gameId to the game page

            startActivity(intent);
        });


        authorUsername.setOnClickListener(v -> {
            if(authorId == null) {
                Toast.makeText(this, "No author Exists", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Activity_Guide.this, Activity_Profile.class);
            intent.putExtra("authorId", authorId); // Pass the authorId to the profile page

            startActivity(intent);
        });
    }

    private void findViews() {
        guideTitleTextView = findViewById(R.id.guideTitleTextView);
        gameNameLink = findViewById(R.id.gameNameLink);
        guideContentWebView = findViewById(R.id.guideContentWebView);
        fabEditGuide = findViewById(R.id.fabEditGuide);
        authorUsername = findViewById(R.id.authorUsername);
    }


    private void loadGuideData() {
        if (guideId != null && !guideId.isEmpty()) {
            db.collection("guides").document(guideId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            guide = documentSnapshot.toObject(Guide.class);
                            if (guide != null) {
                                populateGuideData(guide);
                            }
                        } else {
                            Toast.makeText(this, "Guide not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to load guide", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No Guide ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addGuideToUserRecent(String guideId){
        Log.d("Activity_Guide_recent", "Adding guide to user's recent guides");
        DbManager.getCurrentUser(user -> {
            if(user != null){
                DbManager.addRecentGuide(user.getId(), guideId, isAdd -> {
                    if(isAdd){
                        Log.d("Activity_Guide", "Guide added to user's recent guides");
                    }else{
                        Log.e("Activity_Guide", "Failed to add guide to user's recent guides");
                    }
                });
            }else{
                Log.e("Activity_Guide", "Failed to get current user");
            }
        });
    }

    private void populateGuideData(Guide guide) {
        guideTitleTextView.setText(guide.getTitle());
        gameNameLink.setText(guide.getGameName());

        guideContentWebView.loadDataWithBaseURL(null, guide.getContent(), "text/html", "UTF-8", null);


        if(mAuth.getCurrentUser() == null){
            return;
        }
        if (guide.getAuthorId().equals(mAuth.getCurrentUser().getUid())) {
            fabEditGuide.setVisibility(View.VISIBLE);
        } else {
            fabEditGuide.setVisibility(View.GONE);
        }
    }
}
