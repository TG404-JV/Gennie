package com.android.assistyou;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

public class AnimatedTextView extends androidx.appcompat.widget.AppCompatTextView {

    private String textToAnimate;
    private int delay = 50; // Adjust delay between character reveals (milliseconds)

    public AnimatedTextView(Context context) {
        super(context);
    }

    public void setTextWithAnimation(String text) {
        this.textToAnimate = text;
        animateText(0);
    }

    private void animateText(final int charIndex) {
        if (charIndex >= textToAnimate.length()) {
            return;
        }

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setText(textToAnimate.substring(0, charIndex + 1));
                animateText(charIndex + 1);
            }
        };
        handler.postDelayed(runnable, delay);
    }
}
