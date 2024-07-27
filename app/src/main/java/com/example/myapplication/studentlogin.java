package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class studentlogin extends AppCompatActivity {

    private EditText studentLoginEmailInput, studentLoginPasswordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        mAuth = FirebaseAuth.getInstance();

        studentLoginEmailInput = findViewById(R.id.studentLoginEmailInput);
        studentLoginPasswordInput = findViewById(R.id.studentLoginPasswordInput);
        Button studentLoginButton = findViewById(R.id.studentLoginButton);

        studentLoginButton.setOnClickListener(v -> loginStudent());
    }

    private void loginStudent() {
        String email = studentLoginEmailInput.getText().toString().trim();
        String password = studentLoginPasswordInput.getText().toString().trim();

        if (email.isEmpty()) {
            studentLoginEmailInput.setError("Email is required");
            studentLoginEmailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            studentLoginPasswordInput.setError("Password is required");
            studentLoginPasswordInput.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(studentlogin.this, studentdashboard.class));
                        finish();
                    } else {
                        Toast.makeText(studentlogin.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}

