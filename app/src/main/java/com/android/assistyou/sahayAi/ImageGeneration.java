package com.android.assistyou.sahayAi;

// AssisGemini.java



import static com.android.assistyou.sahayAi.SecureFile.SecureKey.LLMKEY;
import static com.android.assistyou.sahayAi.SecureFile.SecureKey.SAHAY;

import android.graphics.Bitmap;
import android.graphics.Picture;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageGeneration {

    String generatedText;

    public void generatedText(String prompt,Bitmap image, final com.android.assistyou.sahayAi.ImageGeneration.OnTextGeneratedListener listener) {
        // Initialize executor
        Executor executor = Executors.newSingleThreadExecutor();

        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel(SAHAY, LLMKEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);


        Content content = new Content.Builder()
                .addText(prompt)
                .addImage(image)
                .build();


        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Extract the generated text
                generatedText = result.getText();
                // Notify the listener with the generated text
                listener.onTextGenerated(generatedText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }

    public void generatedText(String prompt,Bitmap image1,Bitmap image2, final com.android.assistyou.sahayAi.ImageGeneration.OnTextGeneratedListener listener) {
        // Initialize executor
        Executor executor = Executors.newSingleThreadExecutor();

        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel(SAHAY, LLMKEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);


        Content content = new Content.Builder()
                .addText(prompt)
                .addImage(image1)
                .addImage(image2)
                .build();


        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Extract the generated text
                generatedText = result.getText();
                // Notify the listener with the generated text
                listener.onTextGenerated(generatedText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }


    // Interface to listen for text generation completion
    public interface OnTextGeneratedListener {
        void onTextGenerated(String generatedText);
    }

}

