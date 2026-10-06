package com.campuscare_smartclassattendance;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewStudentsActivity extends AppCompatActivity {

    LinearLayout studentListContainer;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);

        studentListContainer = findViewById(R.id.studentListContainer);

        db = FirebaseFirestore.getInstance();

        loadStudents();
    }

    private void loadStudents() {

        db.collection("attendance")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    studentListContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {

                        TextView emptyText = new TextView(
                                ViewStudentsActivity.this
                        );

                        emptyText.setText("No student records found");
                        emptyText.setTextSize(18);
                        emptyText.setPadding(10, 20, 10, 20);

                        studentListContainer.addView(emptyText);

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

                        TextView studentText = new TextView(
                                ViewStudentsActivity.this
                        );

                        studentText.setText(
                                "Register No : " + registerNumber +
                                        "\nStudent Name : " + studentName +
                                        "\nSubject : " + subject +
                                        "\nDate : " + date +
                                        "\nStatus : " + status
                        );

                        studentText.setTextSize(17);
                        studentText.setPadding(15, 15, 15, 15);

                        studentListContainer.addView(studentText);

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ViewStudentsActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}