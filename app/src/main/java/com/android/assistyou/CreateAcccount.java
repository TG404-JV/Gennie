package com.android.assistyou;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAcccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextMobile;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acccount);

        mAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextMobile = findViewById(R.id.editTextMobile);
        buttonRegister = findViewById(R.id.SignUP);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String mobNumber = editTextMobile.getText().toString().trim();

                if (username.isEmpty()) {
                    editTextUsername.setError("Username is required");
                    editTextUsername.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }

                if (mobNumber.isEmpty()) {
                    editTextMobile.setError("Mobile number is required");
                    editTextMobile.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CreateAcccount.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        storeUserData(user.getUid(), username, email, password, mobNumber);
                                        Intent login =new Intent(CreateAcccount.this, Login.class);
                                        login.putExtra("email",email);
                                        login.putExtra("password",password);
                                        startActivity(login);

                                    }
                                    Toast.makeText(CreateAcccount.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(CreateAcccount.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void storeUserData(String uid, String username, String email, String password, String mobNumber) {
        // Get reference to your Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        // Create a new User object with the provided data
        User user = new User(username, email, password, mobNumber);

        // Set the user data to the database
        databaseReference.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // User data saved successfully
                    // Clear the input fields after successful submission
                    editTextUsername.setText("");
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                    editTextMobile.setText("");

                    Toast.makeText(CreateAcccount.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving user data
                    Toast.makeText(CreateAcccount.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error registering user", e);
                });
    }
}
