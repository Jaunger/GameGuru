package com.daniel.gameguru.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Fragments.GuideListFragment;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Activity_Profile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ShapeableImageView profileImage;
    private MaterialTextView userName;
    private MaterialTextView userDescription;
    private GuideListFragment guideListFragment;
    private AppCompatImageButton editProfileButton;
    private MaterialButton logoutButton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        firestore = FirebaseFirestore.getInstance();

        FragmentManager manager = getSupportFragmentManager();
        guideListFragment = new GuideListFragment();
        manager.beginTransaction().replace(R.id.guideListFragment, guideListFragment).commit();

        findViews();
        initViews();
        updateUI(); //todo: also update description

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
        getSupportFragmentManager().beginTransaction().replace(R.id.guideListFragment, guideListFragment).commit();
        logoutButton.setOnClickListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Activity_Profile.this, Activity_Login.class);
                    startActivity(intent);
                    finish();
                });


        editProfileButton.setOnClickListener(v -> editImage());
    }

    private void editImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    uploadImage(uri);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void uploadImage(Uri uri) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("USERS").child(userUid + ".jpg");

        UploadTask uploadTask = imageRef.putFile(uri);

        uploadTask.addOnFailureListener(exception ->
                        Toast.makeText(Activity_Profile.this, "Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                            String imageUrl = task.getResult().toString();
                            updateUserImageInFirestore(imageUrl);
                        }));
    }

    private void updateUserImageInFirestore(String imageUrl) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userUid);

        userDocRef.update("image", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Activity_Profile.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                    updateUI();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Activity_Profile.this, "Error updating profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void updateUI() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userUid);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    String imageUrl = task.getResult().getString("image");
                    String name = task.getResult().getString("name");

                    Glide.with(Activity_Profile.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.img_white)
                            .centerCrop()
                            .into(profileImage);

                    userName.setText(name);
                }
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }
}
