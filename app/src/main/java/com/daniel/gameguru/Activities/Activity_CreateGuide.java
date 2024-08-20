package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideKeyboard;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class Activity_CreateGuide extends AppCompatActivity {

    public interface FireStoreCallback {
        void onCallback(String gameId);
    }
    private RichEditor mEditor;
    private MaterialButton mBold, mItalic, mUnderline, mInsertImage, mTextColor, mInsertLink, mUndo, mRedo;
    private MaterialButton mSaveAsDraftButton, mPublishButton, mAddImage;
    private BottomNavigationView bottomNavigationView;
    private TextInputEditText guideTitleInput;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView gameNameInput;
    private ArrayAdapter<String> gameNameAdapter;
    private final String TAG = "Activity_CreateGuide";
    private Uri selectedImageUri;
    private int selectedImageWidth;
    private String loadedGuideId;
    private boolean changedColor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_guide);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

       loadedGuideId = getIntent().getStringExtra("guideId");


        findViews();
        initView();

        if (loadedGuideId != null) {
            DbManager.loadGuideData(loadedGuideId, guideData -> {
                if (guideData != null) {
                    guideTitleInput.setText(guideData.getTitle());
                    gameNameInput.setText(guideData.getGameName());
                    mEditor.setHtml(guideData.getContent());
                    if (guideData.getImageUrl() != null) {
                        selectedImageUri = Uri.parse(guideData.getImageUrl());
                    }
                }else {
                    Toast.makeText(this, "Failed to load guide data", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }



    public void setupUI(View view) {
        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (isOpen) {
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(Activity_CreateGuide.this);
                v.clearFocus();
                v.performClick();
                return false;
            });
        }
    }

    private void setupRichEditor() {
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Write your guide...");
    }

    private void findViews() {
        mEditor = findViewById(R.id.editor);
        mBold = findViewById(R.id.action_bold);
        mItalic = findViewById(R.id.action_italic);
        mUnderline = findViewById(R.id.action_underline);
        mInsertImage = findViewById(R.id.action_insert_image);
        mInsertLink = findViewById(R.id.action_insert_link);
        mSaveAsDraftButton = findViewById(R.id.saveAsDraftButton);
        mPublishButton = findViewById(R.id.publishButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mUndo = findViewById(R.id.action_undo);
        mRedo = findViewById(R.id.action_redo);
        mTextColor = findViewById(R.id.action_text_color);
        mAddImage = findViewById(R.id.addImageButton);
        gameNameInput = findViewById(R.id.gameNameInput);
        guideTitleInput = findViewById(R.id.guideTitleInput);
    }
    private void setupAutoComplete() {
        List<String> gameTitles = new ArrayList<>();
        gameNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gameTitles);
        gameNameInput.setAdapter(gameNameAdapter);

        gameNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    fetchGamesFromFireStore(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void fetchGamesFromFireStore(String query) {
        db.collection("games")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + '\uf8ff') // to perform "starts with" query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> titles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            titles.add(document.getString("title"));
                        }
                        gameNameAdapter.clear();
                        gameNameAdapter.addAll(titles);
                        gameNameAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Activity_CreateGuide.this, "Failed to load games", Toast.LENGTH_SHORT).show());
    }

    private void initView() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_create);
        mAddImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
        setupUI(findViewById(R.id.createGuideParent));
        buttonListeners();
        setupRichEditor();
        setupAutoComplete();


    }

    ActivityResultLauncher<PickVisualMediaRequest> pickImageForEditor =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    uploadImageToStorage(uri,1);
                } else {
                    Toast.makeText(Activity_CreateGuide.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    uploadImageToStorage(selectedImageUri,0);
                }
            });


    private void showImageSizeDialog() {
        // Create and configure the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.image_size_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        RadioGroup sizeOptions = dialogView.findViewById(R.id.sizeOptions);
        Button selectImageButton = dialogView.findViewById(R.id.selectImageButton);

        // Handle image selection
        selectImageButton.setOnClickListener(v -> {
            pickImageForEditor.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());

            int selectedSize = sizeOptions.getCheckedRadioButtonId();
            int imageWidth;

            // Set image size based on selection
            if (selectedSize == R.id.radioSmall) {
                imageWidth = 50; // Small size
            } else if (selectedSize == R.id.radioMedium) {
                imageWidth = 100; // Medium size
            } else {
                imageWidth = 200; // Large size
            }

            // Save the image width to use later in uploadImageToStorage method
            selectedImageWidth = imageWidth;

            dialog.dismiss();
        });

        dialog.show();
    }
    private void showLinkInputDialog() {
        // Create and configure the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.insert_link_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextInputEditText linkTextInput = dialogView.findViewById(R.id.linkTextInput);
        TextInputEditText linkUrlInput = dialogView.findViewById(R.id.linkUrlInput);
        Button submitButton = dialogView.findViewById(R.id.submitButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Handle the Submit button click
        submitButton.setOnClickListener(v -> {
            if (linkTextInput.getText() == null || linkUrlInput.getText() == null) {
                Toast.makeText(Activity_CreateGuide.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String linkText = linkTextInput.getText().toString().trim();
            String linkUrl = linkUrlInput.getText().toString().trim();

            if (linkText.isEmpty() || linkUrl.isEmpty()) {
                Toast.makeText(Activity_CreateGuide.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                mEditor.insertLink(linkUrl, linkText);
                dialog.dismiss();
            }
        });
        // Handle the Cancel button click
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void uploadImageToStorage(Uri uri, int type) {
        String path = type == 1 ? "guides/" : "editor/" ;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String imageName = path + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(imageName);

        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    if (type == 0){
                        selectedImageUri = uri1;
                }else if(type == 1){
                        mEditor.insertImage(uri1.toString(), "Image", selectedImageWidth);
                    }
                    Toast.makeText(Activity_CreateGuide.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                }))
                .addOnFailureListener(e -> Toast.makeText(Activity_CreateGuide.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void buttonListeners() {
        mBold.setOnClickListener(v -> {
            mEditor.setBold();
            toggleButtonState(mBold);
        });

        mItalic.setOnClickListener(v -> {
            mEditor.setItalic();
            toggleButtonState(mItalic);
        });

        mUnderline.setOnClickListener(v -> {
            mEditor.setUnderline();
            toggleButtonState(mUnderline);
        });

        mTextColor.setOnClickListener(v -> {
            if(changedColor){
                mEditor.setTextColor(Color.BLACK);
                Toast.makeText(this, "Text color changed", Toast.LENGTH_SHORT).show();
                changedColor = false;
                return;
            }
            changedColor = true;
            mEditor.setTextColor(Color.RED); // Placeholder for color picker implementation
            Toast.makeText(this, "Text color changed", Toast.LENGTH_SHORT).show();
        });

        mInsertImage.setOnClickListener(v -> showImageSizeDialog());

        mInsertLink.setOnClickListener(v -> showLinkInputDialog());


        mUndo.setOnClickListener(v -> mEditor.undo());
        mRedo.setOnClickListener(v -> mEditor.redo());

        mSaveAsDraftButton.setOnClickListener(v -> saveGuide(false));
        mPublishButton.setOnClickListener(v -> saveGuide(true));
    }

    private void toggleButtonState(MaterialButton button) {
        boolean isSelected = button.isSelected();
        button.setSelected(!isSelected);
        button.setAlpha(isSelected ? 1.0f : 0.5f); // Example: change opacity to show selection state
    }
    private void saveGuide(boolean isPublished) {

        if (guideTitleInput.getText() == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = guideTitleInput.getText().toString();
        DbManager.isGuideTitleUnique(title, isUnique -> {
            if (!isUnique && loadedGuideId == null) {
                Toast.makeText(this, "Guide title already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            String gameName = gameNameInput.getText().toString();
            String content = mEditor.getHtml();


        if (title.isEmpty() || gameName.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Retrieve gameId for the selected game
        getGameIdFromName(gameName, gameId -> {
            if (gameId == null) {
                Toast.makeText(this, "Game not found", Toast.LENGTH_SHORT).show();
            } else {
                String guideId = loadedGuideId != null ? loadedGuideId : db.collection("guides").document().getId();
                Map<String, Object> guideData = new HashMap<>();
                guideData.put("id", guideId);
                guideData.put("title", title);
                guideData.put("gameId", gameId); // Associate the guide with the game
                guideData.put("imageUrl", selectedImageUri != null ? selectedImageUri.toString() : null);
                guideData.put("gameName", gameName);
                guideData.put("content", content);
                if(mAuth.getCurrentUser() != null)
                    guideData.put("authorId", mAuth.getCurrentUser().getUid());
                guideData.put("isPublished", isPublished ? "true" : "false");
                guideData.put("timestamp", System.currentTimeMillis());

                db.collection("guides").document(guideId).set(guideData)
                        .addOnSuccessListener(aVoid -> {
                            updateGameWithGuide(gameId, guideId);
                            Toast.makeText(this, isPublished ? "Guide published" : "Guide saved as draft", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, Activity_Profile.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save guide: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    });
}

    private void updateGameWithGuide(String gameId, String guideId) {
        DocumentReference gameRef = db.collection("games").document(gameId);

        gameRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Integer> guideIds = (Map<String, Integer>) documentSnapshot.get("guideIds");
                if (guideIds == null) {
                    guideIds = new HashMap<>();
                }

                guideIds.put(guideId, 1);
                gameRef.update("guideIds", guideIds)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Guide added to game"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding guide to game", e));
            }else{
                Log.d(TAG, "Game not found");
            }
        });


    }
    private void getGameIdFromName(String gameName, FireStoreCallback firestoreCallback) {
        db.collection("games")
                .whereEqualTo("title", gameName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String gameId = task.getResult().getDocuments().get(0).getId();
                        firestoreCallback.onCallback(gameId);
                    } else {
                        firestoreCallback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FireStore", "Error getting documents.", e);
                    firestoreCallback.onCallback(null);
                });
    }
}
