package com.android.assistyou;

public class Suggestion {
    private String text;
    private String imageUrl;

    public Suggestion() {
        // Default constructor required for Firebase
    }

    public Suggestion(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

