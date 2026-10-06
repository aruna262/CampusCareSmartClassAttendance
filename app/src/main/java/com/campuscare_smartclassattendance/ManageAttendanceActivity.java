package com.campuscare_smartclassattendance;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ManageAttendanceActivity extends AppCompatActivity {

    LinearLayout attendanceContainer;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_attendance);

        attendanceContainer = findViewById(R.id.attendanceContainer);

        db = FirebaseFirestore.getInstance();

        loadAttendance();
    }

    private void loadAttendance() {

        db.collection("attendance")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    attendanceContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {

                        TextView emptyText = new TextView(this);

                        emptyText.setText("No attendance records found");
                        emptyText.setTextSize(18);
                        emptyText.setPadding(10, 20, 10, 20);

                        attendanceContainer.addView(emptyText);

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        String studentName =
                                document.getString("studentName");

                        String registerNumber =
                                document.getString("registerNumber");

                        String subject =
                                document.getString("subject");

                        String date =
                                document.getString("date");

                        String status =
                                document.getString("status");

                        TextView attendanceText = new TextView(this);

                        attendanceText.setText(
                                "Register No : " + registerNumber +
                                        "\nStudent : " + studentName +
                                        "\nSubject : " + subject +
                                        "\nDate : " + date +
                                        "\nStatus : " + status +
                                        "\n\nTap to Edit"
                        );

                        attendanceText.setTextSize(17);
                        attendanceText.setPadding(15, 20, 15, 20);

                        attendanceText.setOnClickListener(v -> {

                            showEditDialog(
                                    document.getId(),
                                    studentName,
                                    registerNumber,
                                    subject,
                                    date,
                                    status
                            );

                        });

                        attendanceContainer.addView(attendanceText);
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void showEditDialog(
            String documentId,
            String studentName,
            String registerNumber,
            String subject,
            String date,
            String currentStatus) {

        String[] statusOptions = {
                "Present",
                "Absent"
        };

        int selected = currentStatus.equals("Present") ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle("Edit Attendance")
                .setMessage(
                        "Student : " + studentName +
                                "\nRegister No : " + registerNumber +
                                "\nSubject : " + subject +
                                "\nDate : " + date
                )
                .setSingleChoiceItems(
                        statusOptions,
                        selected,
                        (dialog, which) -> {

                            String newStatus =
                                    statusOptions[which];

                            updateAttendance(
                                    documentId,
                                    newStatus
                            );

                            dialog.dismiss();
                        }
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateAttendance(
            String documentId,
            String newStatus) {

        db.collection("attendance")
                .document(documentId)
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Attendance Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadAttendance();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Update Failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}