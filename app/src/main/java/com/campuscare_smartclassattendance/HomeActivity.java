package com.campuscare_smartclassattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    TextView attendanceText;
    Button btnLogout;

    SharedPreferences preferences;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        attendanceText = findViewById(R.id.attendanceText);
        btnLogout = findViewById(R.id.btnLogout);

        preferences = getSharedPreferences(
                "CampusCare",
                MODE_PRIVATE
        );

        db = FirebaseFirestore.getInstance();

        String role = getIntent().getStringExtra("role");

        if (role != null && role.equals("Student")) {

            String registerNumber =
                    preferences.getString(
                            "registerNumber",
                            ""
                    );

            if (registerNumber.isEmpty()) {

                attendanceText.setText(
                        "Register Number not found"
                );

            } else {

                loadStudentAttendance(registerNumber);
            }
        }

        // Logout
        btnLogout.setOnClickListener(v -> {

            preferences.edit().clear().apply();

            Intent intent = new Intent(
                    HomeActivity.this,
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

    private void loadStudentAttendance(
            String registerNumber) {

        db.collection("attendance")
                .whereEqualTo(
                        "registerNumber",
                        registerNumber
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            // Subject-wise total and present
                            Map<String, Integer> totalMap =
                                    new HashMap<>();

                            Map<String, Integer> presentMap =
                                    new HashMap<>();

                            int totalClasses = 0;
                            int totalPresent = 0;

                            for (
                                    QueryDocumentSnapshot document :
                                    queryDocumentSnapshots
                            ) {

                                String subject =
                                        document.getString(
                                                "subject"
                                        );

                                String status =
                                        document.getString(
                                                "status"
                                        );

                                if (subject == null) {
                                    continue;
                                }

                                // Total classes for subject
                                int total =
                                        totalMap.containsKey(subject)
                                                ? totalMap.get(subject)
                                                : 0;

                                totalMap.put(
                                        subject,
                                        total + 1
                                );

                                totalClasses++;

                                // Present classes
                                if ("Present".equals(status)) {

                                    int present =
                                            presentMap.containsKey(subject)
                                                    ? presentMap.get(subject)
                                                    : 0;

                                    presentMap.put(
                                            subject,
                                            present + 1
                                    );

                                    totalPresent++;
                                }
                            }

                            StringBuilder result =
                                    new StringBuilder();

                            result.append(
                                    "My Attendance\n\n"
                            );

                            if (totalMap.isEmpty()) {

                                result.append(
                                        "No attendance records found."
                                );

                            } else {

                                // Subject-wise display
                                for (String subject :
                                        totalMap.keySet()) {

                                    int total =
                                            totalMap.get(subject);

                                    int present =
                                            presentMap.containsKey(subject)
                                                    ? presentMap.get(subject)
                                                    : 0;

                                    int absent =
                                            total - present;

                                    double percentage =
                                            ((double) present
                                                    / total)
                                                    * 100;

                                    result.append(
                                            subject
                                    );

                                    result.append("\n");

                                    result.append(
                                            "Total Classes : "
                                    );

                                    result.append(total);

                                    result.append("\n");

                                    result.append(
                                            "Present : "
                                    );

                                    result.append(present);

                                    result.append("\n");

                                    result.append(
                                            "Absent : "
                                    );

                                    result.append(absent);

                                    result.append("\n");

                                    result.append(
                                            "Attendance : "
                                    );

                                    result.append(
                                            String.format(
                                                    "%.2f",
                                                    percentage
                                            )
                                    );

                                    result.append("%");

                                    result.append(
                                            "\n\n"
                                    );
                                }

                                // Overall attendance
                                int totalAbsent =
                                        totalClasses -
                                                totalPresent;

                                double overallPercentage = 0;

                                if (totalClasses > 0) {

                                    overallPercentage =
                                            ((double)
                                                    totalPresent
                                                    /
                                                    totalClasses)
                                                    * 100;
                                }

                                result.append(
                                        "--------------------\n"
                                );

                                result.append(
                                        "OVERALL ATTENDANCE\n\n"
                                );

                                result.append(
                                        "Total Classes : "
                                );

                                result.append(
                                        totalClasses
                                );

                                result.append("\n");

                                result.append(
                                        "Present : "
                                );

                                result.append(
                                        totalPresent
                                );

                                result.append("\n");

                                result.append(
                                        "Absent : "
                                );

                                result.append(
                                        totalAbsent
                                );

                                result.append("\n");

                                result.append(
                                        "Overall : "
                                );

                                result.append(
                                        String.format(
                                                "%.2f",
                                                overallPercentage
                                        )
                                );

                                result.append("%");
                            }

                            attendanceText.setText(
                                    result.toString()
                            );
                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            HomeActivity.this,
                            "Error: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}