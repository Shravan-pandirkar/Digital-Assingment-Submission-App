package com.example.digitalassingmentsubmissionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etRegUsername, etRegPassword;
    RadioGroup rgRole;
    RadioButton rbStudent, rbTeacher;
    Button btnRegister, btnBackToLogin;
    LinearLayout llStudentRole, llTeacherRole;
    TextView tvStudentLabel, tvTeacherLabel;
    DatabaseHelper dbHelper;
    boolean isStudentSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            etRegUsername  = findViewById(R.id.etRegUsername);
            etRegPassword  = findViewById(R.id.etRegPassword);
            rgRole         = findViewById(R.id.rgRole);
            rbStudent      = findViewById(R.id.rbStudent);
            rbTeacher      = findViewById(R.id.rbTeacher);
            btnRegister    = findViewById(R.id.btnRegister);
            btnBackToLogin = findViewById(R.id.btnBackToLogin);
            llStudentRole  = findViewById(R.id.llStudentRole);
            llTeacherRole  = findViewById(R.id.llTeacherRole);
            tvStudentLabel = findViewById(R.id.tvStudentLabel);
            tvTeacherLabel = findViewById(R.id.tvTeacherLabel);
            dbHelper       = new DatabaseHelper(this);

            // Animate card
            LinearLayout registerCard = findViewById(R.id.registerCard);
            LinearLayout llLogo       = findViewById(R.id.llLogo);

            if (llLogo != null) {
                llLogo.setAlpha(0f);
                llLogo.setScaleX(0.5f);
                llLogo.setScaleY(0.5f);
                llLogo.animate().alpha(1f).scaleX(1f).scaleY(1f)
                        .setDuration(500).start();
            }
            if (registerCard != null) {
                registerCard.setAlpha(0f);
                registerCard.setTranslationY(80f);
                registerCard.animate().alpha(1f).translationY(0f)
                        .setDuration(600).setStartDelay(200).start();
            }

            // Student role click
            if (llStudentRole != null) {
                llStudentRole.setOnClickListener(v -> {
                    isStudentSelected = true;
                    if (rbStudent != null) rbStudent.setChecked(true);
                    llStudentRole.setBackgroundResource(R.drawable.role_card_selected);
                    if (llTeacherRole != null)
                        llTeacherRole.setBackgroundResource(R.drawable.role_card_unselected);
                    if (tvStudentLabel != null)
                        tvStudentLabel.setTextColor(0xFF1565C0);
                    if (tvTeacherLabel != null)
                        tvTeacherLabel.setTextColor(0xFF90A4AE);
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                            .withEndAction(() -> v.animate().scaleX(1f)
                                    .scaleY(1f).setDuration(80).start()).start();
                });
            }

            // Teacher role click
            if (llTeacherRole != null) {
                llTeacherRole.setOnClickListener(v -> {
                    isStudentSelected = false;
                    if (rbTeacher != null) rbTeacher.setChecked(true);
                    llTeacherRole.setBackgroundResource(R.drawable.role_card_selected);
                    if (llStudentRole != null)
                        llStudentRole.setBackgroundResource(R.drawable.role_card_unselected);
                    if (tvTeacherLabel != null)
                        tvTeacherLabel.setTextColor(0xFF1565C0);
                    if (tvStudentLabel != null)
                        tvStudentLabel.setTextColor(0xFF90A4AE);
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                            .withEndAction(() -> v.animate().scaleX(1f)
                                    .scaleY(1f).setDuration(80).start()).start();
                });
            }

            // Register button
            btnRegister.setOnClickListener(v -> {
                String username = etRegUsername.getText().toString().trim();
                String password = etRegPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    etRegUsername.setError("Enter username");
                    etRegUsername.requestFocus();
                    return;
                }
                if (username.length() < 4) {
                    etRegUsername.setError("At least 4 characters");
                    etRegUsername.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etRegPassword.setError("Enter password");
                    etRegPassword.requestFocus();
                    return;
                }
                if (password.length() < 4) {
                    etRegPassword.setError("At least 4 characters");
                    etRegPassword.requestFocus();
                    return;
                }

                String role = isStudentSelected ? "student" : "teacher";

                if (dbHelper.isUsernameExists(username)) {
                    etRegUsername.setError("Username already taken");
                    etRegUsername.requestFocus();
                    return;
                }

                boolean success = dbHelper.registerUser(username, password, role);

                if (success) {
                    Toast.makeText(this,
                            "✅ Account created! Please login.",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this,
                            "❌ Registration failed. Try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Back to login
            if (btnBackToLogin != null) {
                btnBackToLogin.setOnClickListener(v -> finish());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
