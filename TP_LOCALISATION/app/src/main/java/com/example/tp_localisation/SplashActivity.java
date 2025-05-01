package com.example.tp_localisation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide status bar for full immersion
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_splash);

        // Find views
        ImageView logoImageView = findViewById(R.id.iv_logo);
        TextView appNameTextView = findViewById(R.id.tv_app_name);
        TextView subtitleTextView = findViewById(R.id.tv_app_subtitle);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView versionTextView = findViewById(R.id.tv_app_version);

        // Apply animations
        logoImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_and_scale));
        appNameTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        subtitleTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up_delayed));
        progressBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed));
        versionTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed));

        // Delayed transition to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Close splash activity so it's not in the back stack
        }, SPLASH_DURATION);
    }
}