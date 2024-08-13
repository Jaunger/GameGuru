package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.splashscreen.SplashScreen;


import com.daniel.gameguru.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText loginEmail, loginPassword;
    private AppCompatButton loginButton;
    private AppCompatTextView registerRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setupUI(findViewById(R.id.loginParent));
     //   overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        splashScreen.setKeepOnScreenCondition(() -> false);

        findViews();
        initView();

        mAuth = FirebaseAuth.getInstance();
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Activity_Login.this);
                    v.clearFocus();
                    return false;
                }
            });
        }

    }

    private void initView() {
        loginButton.setOnClickListener(view -> {
            if(loginEmail.getText().toString().isEmpty() || loginPassword.getText().toString().isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            signIn(loginEmail.getText().toString(), loginPassword.getText().toString());
        });
        registerRedirect.setOnClickListener((view) -> {
            Intent intent = new Intent(this, Activity_Register.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }


    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Activity_Login", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(this, Activity_Profile.class);
                        startActivity(intent);
                        finish();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Activity_Login", "signInWithEmail:failure", task.getException());
                        Toast.makeText(Activity_Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
        // [END sign_in_with_email]
    }

    private void findViews(){
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        registerRedirect = findViewById(R.id.registerRedirect);
    }





    private void updateUI(FirebaseUser user) {
        Log.d("Activity_Login", "updateUI: " + user);
    }



    private void reload() { }

}