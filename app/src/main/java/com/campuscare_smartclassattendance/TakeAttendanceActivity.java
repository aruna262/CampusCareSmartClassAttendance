package com.campuscare_smartclassattendance;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeAttendanceActivity extends AppCompatActivity {

    Spinner spinnerSubject;
    TextView txtDate;
    LinearLayout studentContainer;
    Button btnSaveAttendance;

    FirebaseFirestore db;

    String todayDate;

    // Sample students
    String[] studentNames = {
            "Aruna",
            "Bala",
            "Kavin",
            "Priya",
            "Rahul"
    };

    String[] registerNumbers = {
            "101",
            "102",
            "103",
            "104",
            "105"
    };

    List<CheckBox> attendanceCheckBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_attendance);

        // Connect XML views
        spinnerSubject = findViewById(R.id.spinnerSubject);
        txtDate = findViewById(R.id.txtDate);
        studentContainer = findViewById(R.id.studentContainer);
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);

        // Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // -----------------------------
        // SUBJECT LIST
        // -----------------------------

        String[] subjects = {
                "Java",
                "DBMS",
                "Computer Networks",
                "Software Engineering"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        subjects
                );

        spinnerSubject.setAdapter(adapter);

        // -----------------------------
        // CURRENT DATE
        // -----------------------------

        todayDate = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(new Date());

        txtDate.setText(
                "Date: " + todayDate
        );

        // -----------------------------
        // STUDENT LIST
        // -----------------------------

        for (int i = 0; i < studentNames.length; i++) {

            CheckBox checkBox =
                    new CheckBox(this);

            checkBox.setText(
                    registerNumbers[i]
                            + " - "
                            + studentNames[i]
            );

            checkBox.setTextSize(17);

            // Default = Present
            checkBox.setChecked(true);

            studentContainer.addView(
                    checkBox
            );

            attendanceCheckBoxes.add(
                    checkBox
            );
        }

        // -----------------------------
        // SAVE ATTENDANCE
        // -----------------------------

        btnSaveAttendance.setOnClickListener(
                v -> saveAttendance()
        );
    }

    // =====================================================
    // SAVE ATTENDANCE TO FIRESTORE
    // =====================================================

    private void saveAttendance() {

        String subject =
                spinnerSubject
                        .getSelectedItem()
                        .toString();

        // Create Firestore batch
        WriteBatch batch = db.batch();

        // --------------------------------
        // Add every student's attendance
        // --------------------------------

        for (int i = 0;
             i < studentNames.length;
             i++) {

            String status;

            if (attendanceCheckBoxes
                    .get(i)
                    .isChecked()) {

                status = "Present";

            } else {

                status = "Absent";
            }

            // Create attendance data
            Map<String, Object> attendance =
                    new HashMap<>();

            attendance.put(
                    "studentName",
                    studentNames[i]
            );

            attendance.put(
                    "registerNumber",
                    registerNumbers[i]
            );

            attendance.put(
                    "subject",
                    subject
            );

            attendance.put(
                    "date",
                    todayDate
            );

            attendance.put(
                    "status",
                    status
            );

            // Create new Firestore document
            batch.set(
                    db.collection("attendance")
                            .document(),
                    attendance
            );
        }

        // --------------------------------
        // Commit all records
        // --------------------------------

        batch.commit()
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    TakeAttendanceActivity.this,
                                    "Attendance Saved Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // Close page only
                            // after successful save
                            finish();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    TakeAttendanceActivity.this,
                                    "Save Failed: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
}