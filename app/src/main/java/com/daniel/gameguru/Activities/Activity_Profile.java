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
import androidx.fragment.app.FragmentTransaction;


import com.bumptech.glide.Glide;
import com.daniel.gameguru.Fragments.GuideListFragment;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
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






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        findViews();
        initViews();
        setupGuideListFragment();
        updateUI(); //todo: USER image and name arent on the same id

    }

    private void initViews() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_account);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int curItem = item.getItemId();
            if(curItem == R.id.navigation_home){
                startActivity(new Intent(Activity_Profile.this ,Activity_Home.class));
                overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);
                finish();
                return true;
            } else if (curItem == R.id.navigation_account) {

                return true;
            }
            return true;
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

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Activity_Profile.this, "Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String imageUrl = task.getResult().toString();
                        DbManager.updateUserImage(imageUrl, res -> updateUI());
                    }
                });
            }
        });
    }
    private void setupGuideListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        guideListFragment = new GuideListFragment();
        fragmentTransaction.replace(R.id.guideListFragment, guideListFragment);
        fragmentTransaction.commit();
    }

    private void findViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userDescription = findViewById(R.id.userDescription);
        editProfileButton = findViewById(R.id.editProfileButton);
    }


    private void updateUI() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DbManager.getUserImage(imageUrl -> {
            Glide.with(Activity_Profile.this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_white)
                    .centerCrop()
                    .into(profileImage);
        });
        DbManager.getUserName(userUid,name -> {
            Log.d("UserName", "123");
            userName.setText(name);
        });

    }
}