package com.campuscare_smartclassattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StaffActivity extends AppCompatActivity {

    Button btnTakeAttendance;
    Button btnViewStudents;
    Button btnManageAttendance;
    Button btnLogout;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        btnTakeAttendance = findViewById(R.id.btnTakeAttendance);
        btnViewStudents = findViewById(R.id.btnViewStudents);
        btnManageAttendance = findViewById(R.id.btnManageAttendance);
        btnLogout = findViewById(R.id.btnLogout);

        preferences = getSharedPreferences("CampusCare", MODE_PRIVATE);

        // Take Attendance
        btnTakeAttendance.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffActivity.this,
                    TakeAttendanceActivity.class
            );

            startActivity(intent);
        });

        // View Students
        btnViewStudents.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffActivity.this,
                    ViewStudentsActivity.class
            );

            startActivity(intent);

        });
        // Manage Attendance
        btnManageAttendance.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffActivity.this,
                    ManageAttendanceActivity.class
            );

            startActivity(intent);

        });
        // Logout
        btnLogout.setOnClickListener(v -> {

            preferences.edit().clear().apply();

            Intent intent = new Intent(
                    StaffActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}