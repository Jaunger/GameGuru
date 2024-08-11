package com.daniel.gameguru;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText registerName, registerEmail, registerPassword, registerConfirmPassword;
    private AppCompatButton registerButton;
    private AppCompatTextView loginRedirect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        initView();

    }

    private void initView() {
        registerButton.setOnClickListener(view -> {
            if(registerEmail.getText().toString().isEmpty() || registerPassword.getText().toString().isEmpty() || registerName.getText().toString().isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(registerPassword.getText().toString().equals(registerConfirmPassword.getText().toString())){
                createAccount(registerEmail.getText().toString(), registerPassword.getText().toString(), registerName.getText().toString());
            }else{
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
        loginRedirect.setOnClickListener((view) -> {
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }




    private void createAccount(String email, String password,String name) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("RegisterActivity"  , "createUserWithEmail:success");
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("users");

                            User user = new User();
                            user.setId(firebaseUser.getUid())
                                    .setName(name);
                            usersRef.child(firebaseUser.getUid()).setValue(user);
                            Toast.makeText(Activity_Register.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
                            startActivity(intent);
                            finish();
                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                            if(!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    registerPassword.setError("Password Should be at least 6 characters");
                                    registerPassword.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    registerEmail.setError("Invalid Email");
                                    registerEmail.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    registerEmail.setError("Email already in use");
                                    registerEmail.requestFocus();
                                } catch(Exception e) {
                                    Log.e("RegisterActivity", e.getMessage());
                                }
                            }
                            Toast.makeText(Activity_Register.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }








    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }
}