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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_Guide extends AppCompatActivity {

    private MaterialTextView guideTitleTextView, categoryTextView;
    private WebView guideContentWebView;
    private MaterialTextView gameNameLink;
    private String gameId;
    private FloatingActionButton fabEditGuide;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String guideId;
    private Guide guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//TODO: add search functionality
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get guideId from Intent
        guideId = getIntent().getStringExtra("guideId");
        gameId = getIntent().getStringExtra("gameId");
        Log.d("Activity_Guide_GAMe", "Starting edit activity with guideId: " + gameId);

        findViews();
        initViews();
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadGuideData();

        // Setup edit button (only if the user is the author)
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
                // Check if the URL is a valid link and open it in the external browser
                if (URLUtil.isValidUrl(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true; // Indicate that the event is handled
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
    }

    private void findViews() {
        guideTitleTextView = findViewById(R.id.guideTitleTextView);
        gameNameLink = findViewById(R.id.gameNameLink);
        categoryTextView = findViewById(R.id.categoryTextView);
        guideContentWebView = findViewById(R.id.guideContentWebView);
        //guideImageView = findViewById(R.id.guideImageView);
        fabEditGuide = findViewById(R.id.fabEditGuide);

    }



    private void filterList(String text) {
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

    private void populateGuideData(Guide guide) {
        guideTitleTextView.setText(guide.getTitle());
        gameNameLink.setText(guide.getGameName());
        categoryTextView.setText(guide.getCategory());

        // Load HTML content into the WebView
        guideContentWebView.loadDataWithBaseURL(null, guide.getContent(), "text/html", "UTF-8", null);



        // Show or hide the edit button based on the user's ownership of the guide
        if (guide.getAuthorId().equals(mAuth.getCurrentUser().getUid())) {
            fabEditGuide.setVisibility(View.VISIBLE);
        } else {
            fabEditGuide.setVisibility(View.GONE);
        }
    }
}
