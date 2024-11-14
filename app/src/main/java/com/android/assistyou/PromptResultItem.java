package com.android.assistyou;

public class PromptResultItem {
    private String result;
    private String question;

    // Required no-argument constructor for Firebase deserialization
    public PromptResultItem() {
        // Default constructor required for Firebase deserialization
    }

    public PromptResultItem(String result) {
        this.result = result;
    }

    public PromptResultItem(String result, String question) {
        this.result = result;
        this.question = question;
    }

    public PromptResultItem(Object generatedText, String question) {
        this.result = result;
        this.question = question;
    }

    public String getResult() {
        // Remove asterisks from the result string
        if (result.contains("*") || result.contains("**") || result.contains("***")) {
            result = result.replace("*", "");
            result = result.replace("**", "");
            result = result.replace("***", "");
            result = result.replace("#", "");
            result = result.replace("##", "");
            result = result.replace("###", "");


        }
        return result;
    }

    public String getQuestion() {
        return question;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
