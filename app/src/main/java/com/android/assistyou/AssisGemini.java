// AssisGemini.java
package com.android.assistyou;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AssisGemini {

  private static final String apiKey = "AIzaSyCG34Q8Y7USJbk3BVYluNy217GqeU67MSw";

    // Access your API key as a Build Configuration variable
    String generatedText;

    public void generatedText(String prompt, final OnTextGeneratedListener listener) {
        // Initialize executor
        Executor executor = Executors.newSingleThreadExecutor();

        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel("gemini-pro", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(prompt)// Use the provided prompt
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
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }

    // Interface to listen for text generation completion
    public interface OnTextGeneratedListener {
        void onTextGenerated(String generatedText);
    }



}
