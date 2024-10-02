package com.android.assistyou;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Get references to logo and app name TextViews
        final ImageView logoImageView = findViewById(R.id.genie_logo);
        final TextView appNameTextView = findViewById(R.id.app_name);

        // Create animations
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.8f, 1.0f);
        logoScaleX.setDuration(1500);
        logoScaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.8f, 1.0f);
        logoScaleY.setDuration(1500);
        logoScaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator appNameScaleX = ObjectAnimator.ofFloat(appNameTextView, "scaleX", 0.8f, 1.0f);
        appNameScaleX.setDuration(1500);
        appNameScaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator appNameScaleY = ObjectAnimator.ofFloat(appNameTextView, "scaleY", 0.8f, 1.0f);
        appNameScaleY.setDuration(1500);
        appNameScaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator logoFadeOut = ObjectAnimator.ofFloat(logoImageView, "alpha", 1.0f, 0.0f);
        logoFadeOut.setDuration(1500);
        logoFadeOut.setStartDelay(1000); // Start after a delay

        ObjectAnimator appNameFadeOut = ObjectAnimator.ofFloat(appNameTextView, "alpha", 1.0f, 0.0f);
        appNameFadeOut.setDuration(1500);
        appNameFadeOut.setStartDelay(1000); // Start after a delay

        // Combine animations into a set
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(logoScaleX, logoScaleY, appNameScaleX, appNameScaleY, logoFadeOut, appNameFadeOut);

        // Start the animation with a listener to redirect on completion
        animationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Redirect to MainActivity after animation finishes
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animationSet.start();
    }
}
