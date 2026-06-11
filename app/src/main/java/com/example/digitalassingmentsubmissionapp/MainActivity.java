package com.example.digitalassingmentsubmissionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    TextView tvWelcome;
    MaterialCardView cardSubmit, cardLogout;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome   = findViewById(R.id.tvWelcome);
        cardSubmit  = findViewById(R.id.cardSubmit);
        cardLogout  = findViewById(R.id.cardLogout);

        tvWelcome.setText("Welcome, " + username + "! 👋");

        // Entrance animations
        cardSubmit.setAlpha(0f);
        cardLogout.setAlpha(0f);
        cardSubmit.animate().alpha(1f).translationYBy(-30f).setDuration(500).setStartDelay(300).start();
        cardLogout.animate().alpha(1f).translationYBy(-30f).setDuration(500).setStartDelay(450).start();

        // Submit Assignment
        cardSubmit.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                Intent intent = new Intent(this, SubmitAssignmentActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.fade_in);
            }).start();
        });

        // Logout
        cardLogout.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                Toast.makeText(this, "Logged out 👋", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                finish();
            }).start();
        });
    }
}