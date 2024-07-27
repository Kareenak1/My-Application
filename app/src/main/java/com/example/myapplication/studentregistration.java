package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class studentregistration extends AppCompatActivity {

    private EditText studentEmailInput, studentRegNumberInput, studentPasswordInput, studentConfirmPasswordInput;
    private FirebaseAuth mAuth;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        mAuth = FirebaseAuth.getInstance();

        studentEmailInput = findViewById(R.id.studentEmailInput);
        studentRegNumberInput = findViewById(R.id.studentRegNumberInput);
        studentPasswordInput = findViewById(R.id.studentPasswordInput);
        studentConfirmPasswordInput = findViewById(R.id.studentConfirmPasswordInput);
        Button studentRegisterButton = findViewById(R.id.studentRegisterButton);

        studentRegisterButton.setOnClickListener(v -> registerStudent());
    }

    private void registerStudent() {
        String email = studentEmailInput.getText().toString().trim();
        String regNumber = studentRegNumberInput.getText().toString().trim();
        String password = studentPasswordInput.getText().toString().trim();
        String confirmPassword = studentConfirmPasswordInput.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.contains("@students.dkut.ac.ke")) {
            studentEmailInput.setError("Invalid email format");
            studentEmailInput.requestFocus();
            return;
        }

        if (regNumber.isEmpty()) {
            studentRegNumberInput.setError("Registration number is required");
            studentRegNumberInput.requestFocus();
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            studentPasswordInput.setError("Password must be at least 8 characters long, contain at least 2 letters, and 2 symbols");
            studentPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            studentConfirmPasswordInput.setError("Passwords do not match");
            studentConfirmPasswordInput.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(regNumber)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(studentregistration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(studentregistration.this, studentlogin.class));
                                            finish();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(studentregistration.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
