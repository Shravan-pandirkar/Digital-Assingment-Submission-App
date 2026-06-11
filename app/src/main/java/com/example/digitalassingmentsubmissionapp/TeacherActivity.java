package com.example.digitalassingmentsubmissionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class TeacherActivity extends AppCompatActivity {

    MaterialCardView cardViewSubmissions, cardLogout;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        username = getIntent().getStringExtra("username");

        cardViewSubmissions = findViewById(R.id.cardViewSubmissions);
        cardLogout          = findViewById(R.id.cardLogout);

        // Entrance animations
        cardViewSubmissions.setAlpha(0f);
        cardLogout.setAlpha(0f);
        cardViewSubmissions.animate().alpha(1f).translationYBy(-30f).setDuration(500).setStartDelay(300).start();
        cardLogout.animate().alpha(1f).translationYBy(-30f).setDuration(500).setStartDelay(450).start();

        // View Submissions
        cardViewSubmissions.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                startActivity(new Intent(this, SubmissionsActivity.class));
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