package com.daniel.gameguru.Activities;

import static android.app.PendingIntent.getActivity;
import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import jp.wasabeef.richeditor.RichEditor;

import androidx.core.view.WindowInsetsCompat;

import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.HashMap;
import java.util.Map;

public class Activity_CreateGuide extends AppCompatActivity { //TODo: add a way to add images to the guide, add link picker, add text color picker, and scroll

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
        // Set up touch listener for non-text box views to hide keyboard.
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        Log.d(TAG, "onVisibilityChanged: Keyboard visibility changed");
                        if (isOpen) {
                            Log.d(TAG, "onVisibilityChanged: Keyboard is open");
                            bottomNavigationView.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onVisibilityChanged: NavBar got Invisible");
                        } else {
                            Log.d(TAG, "onVisibilityChanged: Keyboard is closed");
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onVisibilityChanged: NavBar got Visible");
                        }
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
        mEditor = (RichEditor) findViewById(R.id.editor);
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

        // Text color change action
        mTextColor.setOnClickListener(v -> {
            mEditor.setTextColor(Color.RED); // Placeholder: Implement a color picker
            Toast.makeText(this, "Text color changed", Toast.LENGTH_SHORT).show();
        });

        // Insert image action
        mInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder - Implement image insertion logic
                mEditor.insertImage("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg", "Image description", 320);
            }
        });


        // Insert link action
        mInsertLink.setOnClickListener(v -> {
            // Placeholder - Implement link insertion logic
            mEditor.insertLink("https://example.com", "Example Link");
            Toast.makeText(this, "Insert Link Clicked", Toast.LENGTH_SHORT).show();
        });

        // Undo action
        mUndo.setOnClickListener(v -> mEditor.undo());

        // Redo action
        mRedo.setOnClickListener(v -> mEditor.redo());

        // Save as Draft action
        mSaveAsDraftButton.setOnClickListener(v -> saveAsDraft());

        // Publish action
        mPublishButton.setOnClickListener(v -> publishGuide());

    }

    private void saveAsDraft() {
        String title = ((TextInputEditText) findViewById(R.id.guideTitleInput)).getText().toString();
        String gameName = ((TextInputEditText) findViewById(R.id.gameNameInput)).getText().toString();
        String category = ((TextInputEditText) findViewById(R.id.categoryInput)).getText().toString();
        String content = mEditor.getHtml();

        if (title.isEmpty() || gameName.isEmpty() || category.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> guideData = new HashMap<>();
        guideData.put("title", title);
        guideData.put("gameName", gameName);
        guideData.put("category", category);
        guideData.put("content", content);
        guideData.put("authorId", mAuth.getCurrentUser().getUid());
        guideData.put("isPublished", false);
        guideData.put("timestamp", System.currentTimeMillis());

        db.collection("guides").add(guideData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Guide saved as draft", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, Activity_Profile.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save draft: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void publishGuide() {
        String title = ((TextInputEditText) findViewById(R.id.guideTitleInput)).getText().toString();
        String gameName = ((TextInputEditText) findViewById(R.id.gameNameInput)).getText().toString();
        String category = ((TextInputEditText) findViewById(R.id.categoryInput)).getText().toString();
        String content = mEditor.getHtml();
        if (title.isEmpty() || gameName.isEmpty() || category.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("guides").add(new HashMap<>())
                .addOnSuccessListener(documentReference -> {
                    // Set the guide ID
                    String guideId = documentReference.getId();

                    // Create guide data
                    Map<String, Object> guideData = new HashMap<>();
                    guideData.put("id", guideId);
                    guideData.put("title", title);
                    guideData.put("gameName", gameName);
                    guideData.put("category", category);
                    guideData.put("content", content);
                    guideData.put("authorId", mAuth.getCurrentUser().getUid());
                    guideData.put("isPublished", true);
                    guideData.put("timestamp", System.currentTimeMillis());

                    // Update the guide document with the complete data
                    documentReference.set(guideData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Guide published", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, Activity_Profile.class);
                                startActivity(intent);
                                finish();                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to publish guide: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create guide: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}