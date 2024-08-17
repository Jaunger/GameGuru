package com.daniel.gameguru.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Activity_EditProfile extends AppCompatActivity {

    private ImageView profileImageEdit;
    private TextInputEditText editNameInput, editDescriptionInput;
    private MaterialButton saveProfileButton;
    private ImageButton returnButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        findViews();
        loadUserData();
        initViews();
    }

    private void findViews() {
        profileImageEdit = findViewById(R.id.profileImageEdit);
        editNameInput = findViewById(R.id.editNameInput);
        editDescriptionInput = findViewById(R.id.editDescriptionInput);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        returnButton = findViewById(R.id.returnButton);
    }

    private void initViews() {
        profileImageEdit.setOnClickListener(v -> editImage());

        saveProfileButton.setOnClickListener(v -> saveUserProfile());

        returnButton.setOnClickListener(v -> {
            Intent i = new Intent(this, Activity_Profile.class);
            startActivity(i);
            finish();
        });
    }
    private void loadUserData() {
        String userUid = mAuth.getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userUid);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String name = task.getResult().getString("name");
                String description = task.getResult().getString("description");
                String imageUrl = task.getResult().getString("image");

                editNameInput.setText(name);
                editDescriptionInput.setText(description);

                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.img_white)
                        .into(profileImageEdit);
            }
        });
    }



    private void editImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImageEdit.setImageURI(uri);
                }
            });

    private void saveUserProfile() {
        String name = editNameInput.getText().toString();
        String description = editDescriptionInput.getText().toString().trim();
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUid = mAuth.getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userUid);

        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("USERS").child(userUid + ".jpg");

                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateUserProfile(userDocRef, name, description, uri.toString());
                        }))
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());

        } else {
            updateUserProfile(userDocRef, name, description, null);
        }
        Intent i = new Intent(this, Activity_Profile.class);
        startActivity(i);
        finish();
    }

    private void updateUserProfile(DocumentReference userDocRef, String name, String description, String imageUrl) {
        if (imageUrl != null) {
            userDocRef.update("image", imageUrl).addOnFailureListener(e -> Toast.makeText(this, "Failed to update image", Toast.LENGTH_SHORT).show());
        }
        userDocRef.update("name", name, "description", description)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}
