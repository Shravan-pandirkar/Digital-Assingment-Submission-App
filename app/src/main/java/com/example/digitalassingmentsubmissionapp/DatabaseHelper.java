package com.example.digitalassingmentsubmissionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "digital_assignments.db";
    private static final int DATABASE_VERSION = 5; // ✅ Incremented to trigger onUpgrade

    // Assignment Table
    private static final String TABLE_ASSIGNMENT    = "assignment";
    private static final String COLUMN_ID           = "id";
    private static final String COLUMN_TITLE        = "title";
    private static final String COLUMN_DESCRIPTION  = "description";
    private static final String COLUMN_DUE_DATE     = "due_date";
    private static final String COLUMN_STUDENT      = "student";

    // Submission Table
    private static final String TABLE_SUBMISSION        = "submissions";
    private static final String COLUMN_SUB_ID           = "sub_id";
    private static final String COLUMN_STUDENT_NAME     = "student_name";
    private static final String COLUMN_ROLL_NO          = "roll_no";
    private static final String COLUMN_ENROLLMENT       = "enrollment";
    private static final String COLUMN_DATE             = "date";
    private static final String COLUMN_SUBJECT          = "subject";
    private static final String COLUMN_TEACHER_USERNAME = "teacher_username";
    private static final String COLUMN_SUBMITTED_BY     = "submitted_by";
    private static final String COLUMN_FILE_PATH        = "file_path"; // ✅ NEW

    // Users Table
    private static final String TABLE_USERS     = "users";
    private static final String COLUMN_USER_ID  = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE     = "role";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called — creating all tables");

        // Assignment table
        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENT + " (" +
                COLUMN_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE       + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_DUE_DATE    + " TEXT," +
                COLUMN_STUDENT     + " TEXT)");

        // Submissions table ✅ NOW includes file_path
        db.execSQL("CREATE TABLE " + TABLE_SUBMISSION + " (" +
                COLUMN_SUB_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_STUDENT_NAME     + " TEXT NOT NULL," +
                COLUMN_ROLL_NO          + " TEXT NOT NULL," +
                COLUMN_ENROLLMENT       + " TEXT NOT NULL," +
                COLUMN_DATE             + " TEXT NOT NULL," +
                COLUMN_SUBJECT          + " TEXT NOT NULL," +
                COLUMN_TEACHER_USERNAME + " TEXT NOT NULL," +
                COLUMN_SUBMITTED_BY     + " TEXT NOT NULL," +
                COLUMN_FILE_PATH        + " TEXT)");

        // Users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT UNIQUE NOT NULL," +
                COLUMN_PASSWORD + " TEXT NOT NULL," +
                COLUMN_ROLE     + " TEXT NOT NULL)");

        Log.d(TAG, "All tables created successfully");
        insertDefaultUsers(db);
    }

    private void insertDefaultUsers(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, "student1");
        cv.put(COLUMN_PASSWORD, "1234");
        cv.put(COLUMN_ROLE, "student");
        db.insert(TABLE_USERS, null, cv);

        cv.clear();
        cv.put(COLUMN_USERNAME, "teacher1");
        cv.put(COLUMN_PASSWORD, "1234");
        cv.put(COLUMN_ROLE, "teacher");
        db.insert(TABLE_USERS, null, cv);

        Log.d(TAG, "Default users inserted: student1, teacher1");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + oldVersion + " → " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBMISSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== Assignment Methods ====================

    public boolean insertAssignment(String title, String description, String dueDate) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_TITLE, title);
            cv.put(COLUMN_DESCRIPTION, description);
            cv.put(COLUMN_DUE_DATE, dueDate);
            cv.put(COLUMN_STUDENT, "");
            long result = db.insert(TABLE_ASSIGNMENT, null, cv);
            Log.d(TAG, "insertAssignment result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "insertAssignment error: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public boolean deleteAssignment(int id) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int rows = db.delete(TABLE_ASSIGNMENT, COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)});
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "deleteAssignment error: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public ArrayList<Assignment> getAllAssignments() {
        ArrayList<Assignment> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ASSIGNMENT +
                    " ORDER BY " + COLUMN_ID + " DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new Assignment(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getAllAssignments error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return list;
    }

    // ==================== Submission Methods ====================

    // ✅ UPDATED — now accepts filePath
    public boolean insertSubmission(String studentName, String rollNo, String enrollment,
                                    String date, String subject,
                                    String teacherUsername, String submittedBy,
                                    String filePath) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_STUDENT_NAME,     studentName);
            cv.put(COLUMN_ROLL_NO,          rollNo);
            cv.put(COLUMN_ENROLLMENT,       enrollment);
            cv.put(COLUMN_DATE,             date);
            cv.put(COLUMN_SUBJECT,          subject);
            cv.put(COLUMN_TEACHER_USERNAME, teacherUsername);
            cv.put(COLUMN_SUBMITTED_BY,     submittedBy);
            cv.put(COLUMN_FILE_PATH,        filePath); // ✅ Save file URI
            long result = db.insert(TABLE_SUBMISSION, null, cv);
            Log.d(TAG, "insertSubmission result: " + result +
                    " | teacher=" + teacherUsername + " | user=" + submittedBy +
                    " | file=" + filePath);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "insertSubmission error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public boolean deleteSubmission(int subId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int rows = db.delete(TABLE_SUBMISSION, COLUMN_SUB_ID + "=?",
                    new String[]{String.valueOf(subId)});
            Log.d(TAG, "deleteSubmission subId=" + subId + " rows=" + rows);
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "deleteSubmission error: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    // ✅ UPDATED — reads filePath from cursor
    public ArrayList<Submission> getAllSubmissions() {
        ArrayList<Submission> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_SUBMISSION +
                            " ORDER BY " + COLUMN_SUB_ID + " DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(new Submission(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUB_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLL_NO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_USERNAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMITTED_BY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH)) // ✅ NEW
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
            Log.d(TAG, "getAllSubmissions: " + list.size() + " records");
        } catch (Exception e) {
            Log.e(TAG, "getAllSubmissions error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return list;
    }

    // ✅ UPDATED — reads filePath from cursor
    public ArrayList<Submission> getSubmissionsForTeacher(String teacherUsername) {
        ArrayList<Submission> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_SUBMISSION + " WHERE " +
                            COLUMN_TEACHER_USERNAME + "=? ORDER BY " + COLUMN_SUB_ID + " DESC",
                    new String[]{teacherUsername});
            if (cursor.moveToFirst()) {
                do {
                    list.add(new Submission(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUB_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLL_NO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_USERNAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMITTED_BY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH)) // ✅ NEW
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getSubmissionsForTeacher error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return list;
    }

    // ✅ UPDATED — reads filePath from cursor
    public ArrayList<Submission> getSubmissionsByStudent(String submittedBy) {
        ArrayList<Submission> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_SUBMISSION + " WHERE " +
                            COLUMN_SUBMITTED_BY + "=? ORDER BY " + COLUMN_SUB_ID + " DESC",
                    new String[]{submittedBy});
            if (cursor.moveToFirst()) {
                do {
                    list.add(new Submission(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUB_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLL_NO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_USERNAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMITTED_BY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH)) // ✅ NEW
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getSubmissionsByStudent error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return list;
    }

    public ArrayList<String> getAllSubmissionsFormatted() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_SUBMISSION +
                            " ORDER BY " + COLUMN_SUB_ID + " DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    String studentName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME));
                    String rollNo      = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLL_NO));
                    String enrollment  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT));
                    String date        = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    String subject     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT));
                    String teacher     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_USERNAME));
                    String submittedBy = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMITTED_BY));
                    String filePath    = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH)); // ✅ NEW

                    String formatted =
                            "<b><font color='#1565C0'>👤 Name:</font></b> " +
                                    "<font color='#212121'>" + studentName + "</font><br>" +
                                    "<b><font color='#1565C0'>📋 Roll No:</font></b> " +
                                    "<font color='#212121'>" + rollNo + "</font><br>" +
                                    "<b><font color='#1565C0'>🆔 Enrollment:</font></b> " +
                                    "<font color='#212121'>" + enrollment + "</font><br>" +
                                    "<b><font color='#1565C0'>📚 Subject:</font></b> " +
                                    "<font color='#E65100'>" + subject + "</font><br>" +
                                    "<b><font color='#1565C0'>👨‍🏫 Teacher:</font></b> " +
                                    "<font color='#388E3C'>" + teacher + "</font><br>" +
                                    "<b><font color='#1565C0'>🔑 Username:</font></b> " +
                                    "<font color='#6A1B9A'>" + submittedBy + "</font><br>" +
                                    "<b><font color='#1565C0'>📅 Date:</font></b> " +
                                    "<font color='#C62828'>" + date + "</font><br>" +
                                    "<b><font color='#1565C0'>📎 File:</font></b> " +
                                    "<font color='#00695C'>" + (filePath != null ? filePath : "No file") + "</font>"; // ✅ NEW

                    list.add(formatted);
                } while (cursor.moveToNext());
            }
            cursor.close();
            Log.d(TAG, "getAllSubmissionsFormatted: " + list.size() + " records");
        } catch (Exception e) {
            Log.e(TAG, "getAllSubmissionsFormatted error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return list;
    }

    // ==================== User Methods ====================

    public String validateUser(String username, String password) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT " + COLUMN_ROLE + " FROM " + TABLE_USERS + " WHERE " +
                            COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{username, password});
            if (cursor.moveToFirst()) {
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
                cursor.close();
                Log.d(TAG, "validateUser: " + username + " → " + role);
                return role;
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "validateUser error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    public boolean registerUser(String username, String password, String role) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USERNAME, username);
            cv.put(COLUMN_PASSWORD, password);
            cv.put(COLUMN_ROLE, role);
            long result = db.insert(TABLE_USERS, null, cv);
            Log.d(TAG, "registerUser: " + username + " result=" + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "registerUser error: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                            " WHERE " + COLUMN_USERNAME + "=?",
                    new String[]{username});
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "isUsernameExists error: " + e.getMessage());
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public ArrayList<String> getAllTeacherUsernames() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS +
                            " WHERE " + COLUMN_ROLE + "='teacher'", null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            Log.d(TAG, "getAllTeacherUsernames: " + list.toString());
        } catch (Exception e) {
            Log.e(TAG, "getAllTeacherUsernames error: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return list;
    }
}