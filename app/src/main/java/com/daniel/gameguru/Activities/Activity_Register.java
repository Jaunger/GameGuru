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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatButton;

import com.daniel.gameguru.R;
import com.daniel.gameguru.Entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText registerName, registerEmail, registerPassword, registerConfirmPassword;
    private AppCompatButton registerButton;
    private AppCompatTextView loginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        setupUI(findViewById(R.id.registerParent));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViews();
        initView();
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Activity_Register.this);
                    v.clearFocus();
                    return false;
                }
            });
        }

    }


    private void initView() {
        registerButton.setOnClickListener(view -> {
            if (registerEmail.getText().toString().isEmpty() || registerPassword.getText().toString().isEmpty() || registerName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (registerPassword.getText().toString().equals(registerConfirmPassword.getText().toString())) {
                createAccount(registerEmail.getText().toString(), registerPassword.getText().toString(), registerName.getText().toString());
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

        loginRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(this, Activity_Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void findViews() {
        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.loginRedirect);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        /*
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }

         */
    }

    private void createAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("RegisterActivity", "createUserWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        saveUserToFirestore(firebaseUser.getUid(), name, email);

                        Toast.makeText(Activity_Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
                        startActivity(intent);
                        finish();
                        updateUI(firebaseUser);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                        if (!task.isSuccessful()) {
                            handleAuthError(task);
                            updateUI(null);
                        }
                        Toast.makeText(Activity_Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name,String email) {
        User user = new User();
        user.setId(userId).setName(name);
        user.setEmail(email);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("RegisterActivity", "User data saved successfully"))
                .addOnFailureListener(e -> Log.w("RegisterActivity", "Error saving user data", e));
    }

    private void handleAuthError(Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            try {
                throw task.getException();
            } catch (FirebaseAuthWeakPasswordException e) {
                registerPassword.setError("Password should be at least 6 characters");
                registerPassword.requestFocus();
            } catch (FirebaseAuthInvalidCredentialsException e) {
                registerEmail.setError("Invalid Email");
                registerEmail.requestFocus();
            } catch (FirebaseAuthUserCollisionException e) {
                registerEmail.setError("Email already in use");
                registerEmail.requestFocus();
            } catch (Exception e) {
                Log.e("RegisterActivity", e.getMessage());
            }
        }
        Toast.makeText(Activity_Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void reload() {
        // Redirect to profile if already logged in
        Intent intent = new Intent(this, Activity_Profile.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {
        // Additional UI updates if necessary after account creation
        if (user != null) {
            Log.d("RegisterActivity", "User registered: " + user.getUid());
        }
    }
}
