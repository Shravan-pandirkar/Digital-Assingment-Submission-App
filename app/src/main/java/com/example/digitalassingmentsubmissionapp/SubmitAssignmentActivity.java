package com.example.digitalassingmentsubmissionapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class SubmitAssignmentActivity extends AppCompatActivity {

    private static final String TAG = "SubmitAssignment";

    TextInputEditText etStudentName, etRollNo, etEnrollment, etDate, etSubject;
    TextInputLayout tilDate;
    AutoCompleteTextView ddTeacher;
    Button btnSubmit, btnAttachFile;
    TextView tvFileName;
    LinearLayout btnBack;
    DatabaseHelper dbHelper;
    String loggedInUsername;
    Uri selectedFileUri = null;

    // File picker launcher
    ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();

                    // ✅ Take persistent permission so URI works later in SubmissionsActivity
                    getContentResolver().takePersistableUriPermission(
                            selectedFileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                    String fileName = getFileName(selectedFileUri);
                    tvFileName.setText(fileName);
                    tvFileName.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    tvFileName.setAlpha(0f);
                    tvFileName.animate().alpha(1f).setDuration(400).start();
                    Toast.makeText(this, "✅ File attached: " + fileName, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "File selected: " + fileName + " URI: " + selectedFileUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        // Get username from MainActivity
        loggedInUsername = getIntent().getStringExtra("username");
        Log.d(TAG, "loggedInUsername = " + loggedInUsername);

        // Safety check
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Log.e(TAG, "Username is null! Redirecting to login.");
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Init views
        etStudentName = findViewById(R.id.etStudentName);
        etRollNo      = findViewById(R.id.etRollNo);
        etEnrollment  = findViewById(R.id.etEnrollment);
        etDate        = findViewById(R.id.etDate);
        etSubject     = findViewById(R.id.etSubject);
        tilDate       = findViewById(R.id.tilDate);
        ddTeacher     = findViewById(R.id.ddTeacher);
        btnSubmit     = findViewById(R.id.btnSubmit);
        btnAttachFile = findViewById(R.id.btnAttachFile);
        tvFileName    = findViewById(R.id.tvFileName);
        btnBack       = findViewById(R.id.btnBack);
        dbHelper      = new DatabaseHelper(this);

        // Entrance animations
        animateEntrance();

        // Custom back button
        btnBack.setOnClickListener(v ->
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(80).withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(() -> {
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                        }).start()
                ).start()
        );

        // Load teachers into dropdown
        ArrayList<String> teachers = dbHelper.getAllTeacherUsernames();
        Log.d(TAG, "Teachers found: " + teachers.size() + " → " + teachers);
        if (teachers.isEmpty()) {
            Toast.makeText(this, "⚠️ No teachers found in database", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, teachers);
        ddTeacher.setAdapter(adapter);

        // Date Picker
        etDate.setOnClickListener(v -> showDatePicker());
        tilDate.setEndIconOnClickListener(v -> showDatePicker());

        // File Picker
        btnAttachFile.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(
                            this::openFilePicker
                    ).start()
            ).start();
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(
                            this::handleSubmit
                    ).start()
            ).start();
        });
    }

    // Entrance animations
    private void animateEntrance() {
        btnSubmit.setAlpha(0f);
        btnSubmit.setTranslationY(40f);
        btnSubmit.animate().alpha(1f).translationY(0f)
                .setDuration(500).setStartDelay(500).start();

        tvFileName.setAlpha(0f);
        tvFileName.animate().alpha(1f).setDuration(400).setStartDelay(300).start();
    }

    // Handle form submission
    private void handleSubmit() {
        String studentName = etStudentName.getText().toString().trim();
        String rollNo      = etRollNo.getText().toString().trim();
        String enrollment  = etEnrollment.getText().toString().trim();
        String date        = etDate.getText().toString().trim();
        String subject     = etSubject.getText().toString().trim();
        String teacher     = ddTeacher.getText().toString().trim();

        Log.d(TAG, "=== Submitting ===");
        Log.d(TAG, "studentName=" + studentName);
        Log.d(TAG, "rollNo=" + rollNo);
        Log.d(TAG, "enrollment=" + enrollment);
        Log.d(TAG, "date=" + date);
        Log.d(TAG, "subject=" + subject);
        Log.d(TAG, "teacher=" + teacher);
        Log.d(TAG, "loggedInUsername=" + loggedInUsername);
        Log.d(TAG, "fileUri=" + selectedFileUri);

        // Validate fields
        if (studentName.isEmpty()) {
            etStudentName.setError("Enter student name");
            etStudentName.requestFocus();
            shakeView(etStudentName);
            return;
        }
        if (rollNo.isEmpty()) {
            etRollNo.setError("Enter roll number");
            etRollNo.requestFocus();
            shakeView(etRollNo);
            return;
        }
        if (enrollment.isEmpty()) {
            etEnrollment.setError("Enter enrollment number");
            etEnrollment.requestFocus();
            shakeView(etEnrollment);
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "📅 Please select submission date", Toast.LENGTH_SHORT).show();
            shakeView(tilDate);
            return;
        }
        if (subject.isEmpty()) {
            etSubject.setError("Enter subject");
            etSubject.requestFocus();
            shakeView(etSubject);
            return;
        }
        if (teacher.isEmpty()) {
            Toast.makeText(this, "👨‍🏫 Please select a teacher", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedFileUri == null) {
            Toast.makeText(this, "📎 Please attach your assignment file", Toast.LENGTH_SHORT).show();
            shakeView(btnAttachFile);
            return;
        }

        // ✅ Convert URI to String for database storage
        String fileUriString = selectedFileUri.toString();
        Log.d(TAG, "Saving fileUri to DB: " + fileUriString);

        // Insert into database
        try {
            // ✅ Now passing fileUriString as the 8th argument
            boolean success = dbHelper.insertSubmission(
                    studentName, rollNo, enrollment,
                    date, subject, teacher,
                    loggedInUsername,
                    fileUriString  // ✅ File URI saved to DB
            );

            Log.d(TAG, "insertSubmission result: " + success);

            if (success) {
                // Success animation
                btnSubmit.setText("✅ Submitted!");
                btnSubmit.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#388E3C")));
                btnSubmit.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150)
                        .withEndAction(() -> btnSubmit.animate()
                                .scaleX(1f).scaleY(1f).setDuration(150)
                                .start()).start();

                Toast.makeText(this,
                        "✅ Assignment submitted successfully!",
                        Toast.LENGTH_LONG).show();

                // Reset after 1.5 seconds
                btnSubmit.postDelayed(() -> {
                    clearFields();
                    btnSubmit.setText("SUBMIT ASSIGNMENT");
                    btnSubmit.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#1565C0")));
                }, 1500);

            } else {
                Log.e(TAG, "insertSubmission returned false");
                Toast.makeText(this,
                        "❌ Submission failed. Try again.",
                        Toast.LENGTH_SHORT).show();
                shakeView(btnSubmit);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Shake animation for validation errors
    private void shakeView(android.view.View view) {
        view.animate()
                .translationX(-16f).setDuration(60).withEndAction(() ->
                        view.animate().translationX(16f).setDuration(60).withEndAction(() ->
                                view.animate().translationX(-8f).setDuration(60).withEndAction(() ->
                                        view.animate().translationX(0f).setDuration(60).start()
                                ).start()).start()).start();
    }

    // Show DatePickerDialog
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etDate.setText(date);
                    Log.d(TAG, "Date selected: " + date);
                }, year, month, day);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    // Open file picker
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // ✅ Changed from GET_CONTENT to OPEN_DOCUMENT for persistent URI permission
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/jpeg",
                "image/png"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select Assignment File"));
    }

    // Get readable file name from URI
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(
                    uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            } catch (Exception e) {
                Log.e(TAG, "getFileName error: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "Unknown file";
    }

    // Clear all fields after submission
    private void clearFields() {
        etStudentName.setText("");
        etRollNo.setText("");
        etEnrollment.setText("");
        etDate.setText("");
        etSubject.setText("");
        ddTeacher.setText("");
        tvFileName.setText("No file selected");
        tvFileName.setTextColor(getResources().getColor(android.R.color.darker_gray));
        selectedFileUri = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
    }
}