package com.example.digitalassingmentsubmissionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etUsername, etPassword;
    Button btnLogin, btnGoRegister;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            etUsername    = findViewById(R.id.etUsername);
            etPassword    = findViewById(R.id.etPassword);
            btnLogin      = findViewById(R.id.btnLogin);
            btnGoRegister = findViewById(R.id.btnGoRegister);
            dbHelper      = new DatabaseHelper(this);

            // Animate logo and card
            LinearLayout llLogo   = findViewById(R.id.llLogo);
            LinearLayout loginCard = findViewById(R.id.loginCard);

            if (llLogo != null) {
                llLogo.setAlpha(0f);
                llLogo.setScaleX(0.5f);
                llLogo.setScaleY(0.5f);
                llLogo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500).start();
            }

            if (loginCard != null) {
                loginCard.setAlpha(0f);
                loginCard.setTranslationY(80f);
                loginCard.animate().alpha(1f).translationY(0f)
                        .setDuration(600).setStartDelay(200).start();
            }

            // Login button
            btnLogin.setOnClickListener(v -> {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Enter username and password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String role = dbHelper.validateUser(username, password);

                if (role != null) {
                    Toast.makeText(this, "Welcome " + username + "! 👋",
                            Toast.LENGTH_SHORT).show();
                    if (role.equals("student")) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (role.equals("teacher")) {
                        Intent intent = new Intent(this, TeacherActivity.class);
                        intent.putExtra("username", username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(this, "❌ Invalid username or password",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Register button
            btnGoRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class))
            );

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}