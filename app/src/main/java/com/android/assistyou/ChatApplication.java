package com.android.assistyou;

import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ChatApplication {

    // For text-only input, use the gemini-pro model

    Boolean isCompleted = false;
    GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyCG34Q8Y7USJbk3BVYluNy217GqeU67MSw");
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    String result;

    private OnTextGeneratedListener listener;
    private PromptResultAdapter promptResultAdapter; // Instance of PromptResultAdapter

    public ChatApplication() {

    }

    public ChatApplication(PromptResultAdapter adapter) {
        this.promptResultAdapter = adapter;
    }

    public void generateText(String prompt, final OnTextGeneratedListener listener) {
        this.listener = listener;

        // Create input content
        Content inputContent = new Content.Builder()
                .addText(prompt)
                .build();

        // Use streaming with text-only input
        Publisher<GenerateContentResponse> streamingResponse =
                model.generateContentStream(inputContent);

        streamingResponse.subscribe(new Subscriber<GenerateContentResponse>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GenerateContentResponse generateContentResponse) {
                String chunk = generateContentResponse.getText();

                result +=chunk;



            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                // Do nothing on complete
                isCompleted = true;


            }
        });
    }

    public interface OnTextGeneratedListener {
        void onTextGenerated(String generatedText);
    }
}
