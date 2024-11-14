package com.android.assistyou;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

public class PromptResultAdapter extends RecyclerView.Adapter<PromptResultAdapter.PromptResultViewHolder> {

    private Context context;
    private List<PromptResultItem> resultList;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private Uri imageURI;
    private String userID;
    private TextToSpeech textToSpeech;

    PromptResultAdapter()
    {

    }

    public PromptResultAdapter(Context context, List<PromptResultItem> resultList) {
        this.context = context;
        this.resultList = resultList;
        databaseRef = FirebaseDatabase.getInstance().getReference("prompt_results");
        storageRef = FirebaseStorage.getInstance().getReference();
        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TextToSpeech", "Language not supported");
                    }
                } else {
                    Log.e("TextToSpeech", "Initialization failed");
                }
            }
        });
    }

    @NonNull
    @Override
    public PromptResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prompt_result, parent, false);
        return new PromptResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromptResultViewHolder holder, int position) {
        PromptResultItem item = resultList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Method to update result text at a specific position
    // Method to update result text at a specific position
    public void updateResult(int position, String newData) {
        if (position >= 0 && position < resultList.size()) {
            resultList.get(position).setResult(newData);
            notifyItemChanged(position);
        }
    }

    public class PromptResultViewHolder extends RecyclerView.ViewHolder {
        private TextView resultTextView;
        private TextView promptQuestion;
        private MaterialButton copyBtn;
        private MaterialButton shareBtn, Speech;
        private ShapeableImageView UserProfile;
        private boolean isSpeaking;

        public PromptResultViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(R.id.result_text_view);
            promptQuestion = itemView.findViewById(R.id.Question);
            copyBtn = itemView.findViewById(R.id.copy_btn);
            shareBtn = itemView.findViewById(R.id.share_btn);
            Speech = itemView.findViewById(R.id.TextToSpeech);
            UserProfile = itemView.findViewById(R.id.user_profile);
            isSpeaking = false;

            Speech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = resultTextView.getText().toString();
                    if (textToSpeech != null) {
                        if (!isSpeaking) {
                            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                            isSpeaking = true;
                        } else {
                            if (textToSpeech.isSpeaking()) {
                                textToSpeech.stop();
                                isSpeaking = false;
                            }
                        }
                    }
                }
            });

            copyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = resultTextView.getText().toString();
                    copyToClipboard(text);
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = resultTextView.getText().toString();
                    shareText(text);
                }
            });
        }

        private void copyToClipboard(String text) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            showToast("Text copied to clipboard");
        }

        private void shareText(String text) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }

        private void showToast(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        public void bind(PromptResultItem item) {
            loadProfileImageFromFirebase();
            resultTextView.setText(item.getResult());
            promptQuestion.setText(item.getQuestion());
            String que= item.getQuestion();
            String ans= item.getResult();

            saveResultToFirebase(ans,que);
        }

        private void saveResultToFirebase(String result, String question) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                String key = databaseRef.child(uid).push().getKey();
                if (key != null) {
                    DatabaseReference userRef = databaseRef.child(uid).child(key);
                    userRef.child("result").setValue(result);
                    userRef.child("question").setValue(question)
                            .addOnSuccessListener(aVoid -> Log.d("PromptResultAdapter", "Result data saved to Firebase"))
                            .addOnFailureListener(e -> Log.e("PromptResultAdapter", "Failed to save result data to Firebase: " + e.getMessage()));
                }
            } else {
                Log.e("PromptResultAdapter", "User not authenticated");
            }
        }


        private void loadProfileImageFromFirebase() {
            if (imageURI != null) {
                Glide.with(context).load(imageURI).circleCrop().into(UserProfile);
            } else {
                StorageReference profileImageRef = storageRef.child("profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
                profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            imageURI = uri;
                            Glide.with(context).load(imageURI).circleCrop().into(UserProfile);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TAG", "Failed to download image from Firebase Storage: " + e.getMessage());
                        });
            }
        }



    }


    // Method to add new data to the adapter
    public void addData(String newData) {
        // Create a new PromptResultItem with the new data
        PromptResultItem newItem = new PromptResultItem(newData);
        // Add the new item to the resultList
        resultList.add(newItem);
        // Notify the adapter of the data change
        notifyDataSetChanged();
    }




}

