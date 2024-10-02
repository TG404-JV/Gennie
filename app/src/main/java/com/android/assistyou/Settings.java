package com.android.assistyou;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Settings extends AppCompatActivity {

    private EditText suggestionEditText;
    private Button loginButton;
    private Button createAccountButton;
    private Button logoutButton;
    private Button forgetPasswordButton;
    private ShapeableImageView profile;
    private StorageReference storageRef;
    private Uri profileURI;
    private FirebaseAuth mAuth;
    private ImageView userBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase Auth, Database, and Storage
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Initialize views
        suggestionEditText = findViewById(R.id.userSuggestion);
        Button sendButton = findViewById(R.id.sendBtn);
        loginButton = findViewById(R.id.SignIN);
        createAccountButton = findViewById(R.id.SignUP);
        logoutButton = findViewById(R.id.LogOut);
        profile = findViewById(R.id.SettingUserProfile);
        forgetPasswordButton = findViewById(R.id.ForgetPass);

        profile.setOnClickListener(v -> pickImage());

        // Load profile image if available
        loadProfileImageFromFirebase();

        // Set click listeners
        sendButton.setOnClickListener(v -> sendSuggestion());
        loginButton.setOnClickListener(v -> {
            // Handle login button click
        });
        createAccountButton.setOnClickListener(v -> {
            // Handle create account button click
        });
        logoutButton.setOnClickListener(v -> logout());

        // Update UI based on user authentication state
        updateUI(mAuth.getCurrentUser());

        // Load user data
        userData();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            loginButton.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            forgetPasswordButton.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            createAccountButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            forgetPasswordButton.setVisibility(View.GONE);
        }
    }

    private void sendSuggestion() {
        String suggestion = suggestionEditText.getText().toString().trim();

        if (suggestion.isEmpty()) {
            Toast.makeText(this, "Please enter a suggestion", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("suggestions");
        databaseReference.push().setValue(suggestion)
                .addOnSuccessListener(aVoid -> {
                    suggestionEditText.setText("");
                    Toast.makeText(this, "Suggestion sent successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send suggestion", Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseError", "Error sending suggestion", e);
                });
    }

    private void logout() {
        mAuth.signOut();
        updateUI(null);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void userData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String mobNumber = dataSnapshot.child("mobNumber").getValue(String.class);

                        TextView usernameTextView = findViewById(R.id.usernameTextView);
                        TextView emailTextView = findViewById(R.id.emailTextView);
                        TextView mobileNumberTextView = findViewById(R.id.mobileNumberTextView);

                        usernameTextView.setText(username);
                        emailTextView.setText(email);
                        mobileNumberTextView.setText(mobNumber);
                    }
                } else {
                    Log.e("FirebaseError", "Error getting user data", task.getException());
                }
            });
        } else {
            Log.e("FirebaseError", "No user is currently signed in");
        }
    }

    private void pickImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri profileImageUri = data.getData();
            profile.setImageURI(profileImageUri);
            uploadImageToFirebaseStorage(profileImageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference profileImageRef = storageRef.child("profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("FirebaseStorage", "Image uploaded successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "Failed to upload image to Firebase Storage: " + e.getMessage());
                    });
        }
    }

    private void loadProfileImageFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            StorageReference profileImageRef = storageRef.child("profile_images/" + currentUser.getUid() + ".jpg");
            profileImageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        profileURI = uri;
                        Glide.with(this).load(uri).circleCrop().into(profile);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "Failed to download image from Firebase Storage: " + e.getMessage());
                    });
        }
    }
}
