package com.example.digitalassingmentsubmissionapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SubmissionsActivity extends AppCompatActivity {

    ListView lvSubmissions;
    LinearLayout llEmpty;
    LinearLayout btnBack;
    TextView tvSubmissionCount;
    DatabaseHelper dbHelper;

    ArrayList<Integer> submissionIds = new ArrayList<>();
    ArrayList<String> submissionList = new ArrayList<>();
    ArrayList<String> filePaths = new ArrayList<>(); // ✅ NEW — store file URIs per row

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submissions);

        lvSubmissions     = findViewById(R.id.lvSubmissions);
        llEmpty           = findViewById(R.id.llEmpty);
        btnBack           = findViewById(R.id.btnBack);
        tvSubmissionCount = findViewById(R.id.tvSubmissionCount);
        dbHelper          = new DatabaseHelper(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
            }
        });

        btnBack.setOnClickListener(v ->
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(80).withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(() ->
                                finish()
                        ).start()
                ).start()
        );

        loadSubmissions();
    }

    private void loadSubmissions() {
        submissionIds.clear();
        submissionList.clear();
        filePaths.clear(); // ✅ NEW

        ArrayList<Submission> submissions = dbHelper.getAllSubmissions();

        if (tvSubmissionCount != null) {
            tvSubmissionCount.setText(submissions.size() + " submission(s) found");
        }

        if (submissions.isEmpty()) {
            llEmpty.setVisibility(View.VISIBLE);
            lvSubmissions.setVisibility(View.GONE);
            llEmpty.setAlpha(0f);
            llEmpty.animate().alpha(1f).setDuration(600).start();
        } else {
            llEmpty.setVisibility(View.GONE);
            lvSubmissions.setVisibility(View.VISIBLE);

            for (Submission s : submissions) {
                submissionIds.add(s.getSubId());
                filePaths.add(s.getFilePath()); // ✅ NEW — store URI for this row

                // ✅ NEW — get human-readable file name
                String displayFileName = getFileNameFromUri(s.getFilePath());

                String html =
                        "<b><font color='#1565C0'>👤 Name:</font></b> " +
                                "<font color='#212121'>" + s.getStudentName() + "</font><br>" +
                                "<b><font color='#1565C0'>📋 Roll No:</font></b> " +
                                "<font color='#212121'>" + s.getRollNo() + "</font><br>" +
                                "<b><font color='#1565C0'>🆔 Enrollment:</font></b> " +
                                "<font color='#212121'>" + s.getEnrollment() + "</font><br>" +
                                "<b><font color='#1565C0'>📚 Subject:</font></b> " +
                                "<font color='#E65100'>" + s.getSubject() + "</font><br>" +
                                "<b><font color='#1565C0'>👨‍🏫 Teacher:</font></b> " +
                                "<font color='#388E3C'>" + s.getTeacherUsername() + "</font><br>" +
                                "<b><font color='#1565C0'>🔑 Username:</font></b> " +
                                "<font color='#6A1B9A'>" + s.getSubmittedBy() + "</font><br>" +
                                "<b><font color='#1565C0'>📅 Date:</font></b> " +
                                "<font color='#C62828'>" + s.getDate() + "</font><br>" +
                                // ✅ NEW — show file name in list
                                "<b><font color='#1565C0'>📎 File:</font></b> " +
                                "<font color='#00695C'>" + displayFileName + "</font>";

                submissionList.add(html);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.item_submission, R.id.tvContent, submissionList) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView tvContent     = view.findViewById(R.id.tvContent);
                    TextView tvIndex       = view.findViewById(R.id.tvIndex);
                    LinearLayout btnDelete = view.findViewById(R.id.btnDelete);
                    Button btnOpenFile     = view.findViewById(R.id.btnOpenFile); // ✅ NEW

                    tvContent.setText(Html.fromHtml(
                            getItem(position),
                            Html.FROM_HTML_MODE_COMPACT));

                    tvIndex.setText("#" + (position + 1));

                    view.setAlpha(0f);
                    view.animate()
                            .alpha(1f)
                            .translationYBy(-20f)
                            .setDuration(400)
                            .setStartDelay(position * 80L)
                            .start();

                    // ✅ NEW — Open File button
                    String filePath = filePaths.get(position);
                    if (filePath == null || filePath.isEmpty()) {
                        btnOpenFile.setVisibility(View.GONE);
                    } else {
                        btnOpenFile.setVisibility(View.VISIBLE);
                        btnOpenFile.setOnClickListener(v -> {
                            try {
                                Uri fileUri = Uri.parse(filePath);
                                String mimeType = getContentResolver().getType(fileUri);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(fileUri, mimeType != null ? mimeType : "*/*");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(Intent.createChooser(intent, "Open File With"));
                            } catch (Exception e) {
                                Log.e("SubmissionsActivity", "Open file error: " + e.getMessage());
                                Toast.makeText(SubmissionsActivity.this,
                                        "❌ Cannot open file", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Delete click
                    btnDelete.setOnClickListener(v -> {
                        v.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80)
                                .withEndAction(() -> v.animate()
                                        .scaleX(1f).scaleY(1f).setDuration(80).start())
                                .start();
                        showDeleteDialog(position, view);
                    });

                    return view;
                }
            };

            lvSubmissions.setAdapter(adapter);
        }
    }

    // ✅ NEW — Convert URI string to readable file name
    private String getFileNameFromUri(String uriString) {
        if (uriString == null || uriString.isEmpty()) return "No file attached";
        try {
            Uri uri = Uri.parse(uriString);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String name = cursor.getString(index);
                    cursor.close();
                    return name;
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SubmissionsActivity", "getFileNameFromUri error: " + e.getMessage());
        }
        // Fallback — extract name from URI path
        String path = uriString;
        int cut = path.lastIndexOf('/');
        if (cut != -1) path = path.substring(cut + 1);
        return path.isEmpty() ? "Unknown file" : path;
    }

    private void showDeleteDialog(int position, View itemView) {
        new AlertDialog.Builder(this)
                .setTitle("🗑 Delete Submission")
                .setMessage("Are you sure you want to delete this submission?\nThis cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int subId = submissionIds.get(position);

                    itemView.animate()
                            .translationX(itemView.getWidth())
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(() -> {
                                boolean deleted = dbHelper.deleteSubmission(subId);
                                if (deleted) {
                                    Toast.makeText(this,
                                            "✅ Submission deleted",
                                            Toast.LENGTH_SHORT).show();
                                    loadSubmissions();
                                } else {
                                    Toast.makeText(this,
                                            "❌ Delete failed",
                                            Toast.LENGTH_SHORT).show();
                                    itemView.setTranslationX(0f);
                                    itemView.setAlpha(1f);
                                }
                            }).start();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}