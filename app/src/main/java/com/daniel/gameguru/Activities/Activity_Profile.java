package com.daniel.gameguru.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Adapters.GuideAdapter;
import com.daniel.gameguru.Entities.Guide;
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

import java.util.ArrayList;
import java.util.List;

public class Activity_Profile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ShapeableImageView profileImage;
    private MaterialTextView userName;
    private MaterialTextView userDescription, myGuidesTitle;
    private AppCompatImageButton editProfileButton;
    private MaterialButton logoutButton, followButton;
    private RecyclerView guideRecyclerView;
    private GuideAdapter guideAdapter;
    private List<Guide> guideList;
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

        findViews();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void findViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userDescription = findViewById(R.id.userDescription);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
        followButton = findViewById(R.id.followButton);
        myGuidesTitle = findViewById(R.id.myGuidesTitle);
        guideRecyclerView = findViewById(R.id.guideRecyclerView);
    }

    private void initViews() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_account);

        guideList = new ArrayList<>();
        guideAdapter = new GuideAdapter(guideList);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        guideRecyclerView.setAdapter(guideAdapter);

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Activity_Profile.this, Activity_Login.class);
            startActivity(intent);
            finish();
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Profile.this, Activity_EditProfile.class);
            startActivity(intent);
        });
    }

    private void updateUI() {
        if (userUid == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } else {
                return;
            }
        }
        DocumentReference userDocRef = firestore.collection("users").document(userUid);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
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
                DbManager.isCurrentUser(userUid, isCurrentUser -> {
                    if (isCurrentUser) {
                        editProfileButton.setVisibility(View.VISIBLE);
                        logoutButton.setVisibility(View.VISIBLE);
                        followButton.setVisibility(View.GONE);
                        myGuidesTitle.setText(String.format("%s","My Guides"));
                        loadGuides();
                    } else {
                        editProfileButton.setVisibility(View.GONE);
                        logoutButton.setVisibility(View.GONE);
                        followButton.setVisibility(View.VISIBLE);
                        myGuidesTitle.setText(String.format("%s's Guides", userName.getText().toString()));

                        DbManager.isFollowing(userUid, isFollowing -> followButton.setText(isFollowing ? "Unfollow" : "Follow"));

                        followButton.setOnClickListener(v -> DbManager.toggleFollow(userUid, isFollowing -> followButton.setText(isFollowing ? "Unfollow" : "Follow")));
                        loadGuides();
                    }
                });
            } else {
                Log.d("FireStore", "Error getting documents: ", task.getException());
            }
        });
    }

    private void loadGuides() {
        DbManager.getGuidesByAuthor(userUid, guides -> {
            if (guides != null) {
                guideList.clear();
                guideList.addAll(guides);
                guideAdapter.notifyDataSetChanged();
            } else {
                Log.e("Activity_Profile", "Failed to load guides");
            }
        });
    }
}
