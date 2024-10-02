package com.android.assistyou;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.PromptResultViewHolder> {

    private Context context;
    private List<PromptResultItem> resultList;

    public HistoryAdapter(Context context, List<PromptResultItem> resultList) {
        this.context = context;
        this.resultList = resultList;
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

    public class PromptResultViewHolder extends RecyclerView.ViewHolder {
        private TextView resultTextView, questionTextView;
        private ImageView copyBtn, shareBtn, speechBtn;
        private ShapeableImageView userProfile;
        private TextToSpeech textToSpeech;
        private boolean isSpeaking;

        public PromptResultViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(R.id.result_text_view);
            questionTextView = itemView.findViewById(R.id.Question);
            copyBtn = itemView.findViewById(R.id.copy_btn);
            shareBtn = itemView.findViewById(R.id.share_btn);
            speechBtn = itemView.findViewById(R.id.TextToSpeech);
            userProfile = itemView.findViewById(R.id.user_profile);
            isSpeaking = false;

            textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TextToSpeech", "Language not supported");
                    }
                } else {
                    Log.e("TextToSpeech", "Initialization failed");
                }
            });

            speechBtn.setOnClickListener(v -> {
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
            });

            copyBtn.setOnClickListener(v -> {
                String text = resultTextView.getText().toString();
                copyToClipboard(text);
            });

            shareBtn.setOnClickListener(v -> {
                String text = resultTextView.getText().toString();
                shareText(text);
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
            resultTextView.setText(item.getResult());
            questionTextView.setText(item.getQuestion());
            loadProfileImageFromFirebase();
        }

        private void loadProfileImageFromFirebase() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + uid + ".jpg");
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(context).load(uri).circleCrop().into(userProfile);
                }).addOnFailureListener(e -> {
                    Log.e("HistoryAdapter", "Failed to download image from Firebase Storage: " + e.getMessage());
                    // Set a default image or handle the failure as needed
                });
            }
        }
    }
}
