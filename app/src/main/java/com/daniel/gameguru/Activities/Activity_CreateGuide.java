package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class Activity_CreateGuide extends AppCompatActivity {

    public interface FirestoreCallback {
        void onCallback(String gameId);
    }
    private RichEditor mEditor;
    private MaterialButton mBold, mItalic, mUnderline, mInsertImage, mTextColor, mInsertLink, mUndo, mRedo;
    private MaterialButton mSaveAsDraftButton, mPublishButton;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final String TAG = "Activity_CreateGuide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_guide);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupUI(findViewById(R.id.createGuideParent));
        findViews();
        initView();
        buttonListeners();
        setupRichEditor();
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
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Activity_CreateGuide.this);
                    v.clearFocus();
                    return false;
                }
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
    }

    private void initView() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_create);
    }

    private void buttonListeners() {
        mBold.setOnClickListener(v -> mEditor.setBold());
        mItalic.setOnClickListener(v -> mEditor.setItalic());
        mUnderline.setOnClickListener(v -> mEditor.setUnderline());

        mTextColor.setOnClickListener(v -> {
            mEditor.setTextColor(Color.RED); // Placeholder for color picker implementation
            Toast.makeText(this, "Text color changed", Toast.LENGTH_SHORT).show();
        });

        mInsertImage.setOnClickListener(v -> {
            // Placeholder: Implement actual image selection logic
            mEditor.insertImage("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg", "Image description", 320);
        });

        mInsertLink.setOnClickListener(v -> {
            // Placeholder: Implement actual link insertion logic
            mEditor.insertLink("https://example.com", "Example Link");
            Toast.makeText(this, "Link inserted", Toast.LENGTH_SHORT).show();
        });

        mUndo.setOnClickListener(v -> mEditor.undo());
        mRedo.setOnClickListener(v -> mEditor.redo());

        mSaveAsDraftButton.setOnClickListener(v -> saveGuide(false));
        mPublishButton.setOnClickListener(v -> saveGuide(true));
    }

    private void saveGuide(boolean isPublished) {
        String title = ((TextInputEditText) findViewById(R.id.guideTitleInput)).getText().toString();
        String gameName = ((TextInputEditText) findViewById(R.id.gameNameInput)).getText().toString();
        String category = ((TextInputEditText) findViewById(R.id.categoryInput)).getText().toString();
        String content = mEditor.getHtml();


        if (title.isEmpty() || gameName.isEmpty() || category.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Retrieve gameId for the selected game
        getGameIdFromName(gameName, gameId -> {
            if (gameId == null) {
                Toast.makeText(this, "Game not found", Toast.LENGTH_SHORT).show();
            } else db.collection("guides").add(new HashMap<>())
                    .addOnSuccessListener(documentReference -> {
                        String guideId = documentReference.getId();

                        Map<String, Object> guideData = new HashMap<>();
                        guideData.put("id", guideId);
                        guideData.put("title", title);
                        guideData.put("gameId", gameId); // Associate the guide with the game
                        guideData.put("gameName", gameName);
                        guideData.put("category", category);
                        guideData.put("content", content);
                        guideData.put("authorId", mAuth.getCurrentUser().getUid());
                        guideData.put("isPublished", isPublished);
                        guideData.put("timestamp", System.currentTimeMillis());

                        documentReference.set(guideData)
                                .addOnSuccessListener(aVoid -> {
                                    updateGameWithGuide(gameId, guideId);
                                    Toast.makeText(this, isPublished ? "Guide published" : "Guide saved as draft", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, Activity_Profile.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save guide: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to create guide: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void updateGameWithGuide(String gameId, String guideId) {
        DocumentReference gameRef = db.collection("games").document(gameId);

        gameRef.update("guideIds", FieldValue.arrayUnion(guideId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Guide ID added to game"))
                .addOnFailureListener(e -> Log.d(TAG, "Failed to add guide ID to game: " + e.getMessage()));
    }
    private void getGameIdFromName(String gameName, FirestoreCallback firestoreCallback) {
        db.collection("games")
                .whereEqualTo("title", gameName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming the first document returned is the correct game
                        String gameId = task.getResult().getDocuments().get(0).getId();
                        firestoreCallback.onCallback(gameId);
                    } else {
                        firestoreCallback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting documents.", e);
                    firestoreCallback.onCallback(null);
                });
    }
}
