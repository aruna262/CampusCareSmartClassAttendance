package com.campuscare_smartclassattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStudent, btnStaff;
    EditText edtRegisterNumber;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(
                "CampusCare",
                MODE_PRIVATE
        );

        boolean isLoggedIn =
                preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {

            String role =
                    preferences.getString("role", "");

            if (role.equals("Student")) {

                Intent intent = new Intent(
                        MainActivity.this,
                        HomeActivity.class
                );

                intent.putExtra("role", "Student");

                startActivity(intent);
                finish();
                return;

            } else if (role.equals("Staff")) {

                Intent intent = new Intent(
                        MainActivity.this,
                        StaffActivity.class
                );

                intent.putExtra("role", "Staff");

                startActivity(intent);
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_main);

        btnStudent = findViewById(R.id.btnStudent);
        btnStaff = findViewById(R.id.btnStaff);
        edtRegisterNumber = findViewById(R.id.edtRegisterNumber);

        // Student Login
        btnStudent.setOnClickListener(v -> {

            String registerNumber =
                    edtRegisterNumber.getText()
                            .toString()
                            .trim();

            if (registerNumber.isEmpty()) {

                edtRegisterNumber.setError(
                        "Enter Register Number"
                );

                return;
            }

            preferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("role", "Student")
                    .putString(
                            "registerNumber",
                            registerNumber
                    )
                    .apply();

            Intent intent = new Intent(
                    MainActivity.this,
                    HomeActivity.class
            );

            intent.putExtra("role", "Student");
            intent.putExtra(
                    "registerNumber",
                    registerNumber
            );

            startActivity(intent);
            finish();
        });

        // Staff Login
        btnStaff.setOnClickListener(v -> {

            preferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("role", "Staff")
                    .apply();

            Intent intent = new Intent(
                    MainActivity.this,
                    StaffActivity.class
            );

            intent.putExtra("role", "Staff");

            startActivity(intent);
            finish();
        });
    }
}