package com.daniel.gameguru.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Fragments.GuideListFragment;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_Profile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ShapeableImageView profileImage;
    private MaterialTextView userName;
    private MaterialTextView userDescription;
    private GuideListFragment guideListFragment;
    private AppCompatImageButton editProfileButton;
    private MaterialButton logoutButton;
    private FirebaseFirestore firestore;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        firestore = FirebaseFirestore.getInstance();
        userUid = getIntent().getStringExtra("authorId");
        Log.d("Activity_Profile_1", "Starting edit activity with authorId: " + userUid);


        findViews();
        initViews();
        updateUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void findViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userDescription = findViewById(R.id.userDescription);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void initViews() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_account);
        guideListFragment = new GuideListFragment();
        guideListFragment.setAuthorId(userUid);
        getSupportFragmentManager().beginTransaction().replace(R.id.guideListFragment, guideListFragment).commit();

        logoutButton.setOnClickListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Activity_Profile.this, Activity_Login.class);
                    startActivity(intent);
                    finish();
                });

        DbManager.isCurrentUser(userUid, isCurrentUser -> {
                if (isCurrentUser) {
                editProfileButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.VISIBLE);
            } else {
                editProfileButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.GONE);
            }
                });
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Profile.this, Activity_EditProfile.class);
            startActivity(intent);
        });
    }

    private void updateUI() {
        if(userUid == null)
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userUid);

        userDocRef.get().addOnCompleteListener(task -> { //todo: need to make it to not update late
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    String imageUrl = task.getResult().getString("image");
                    String name = task.getResult().getString("name");
                    String description = task.getResult().getString("description");
                    Glide.with(Activity_Profile.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.img_white)
                            .centerCrop()
                            .into(profileImage);

                    userName.setText(name);
                    userDescription.setText(description);
                }
            } else {
                Log.d("FireStore", "Error getting documents: ", task.getException());
            }
        });
    }
}
