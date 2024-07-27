package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class supervisorlogin extends AppCompatActivity {

    private EditText supervisorLoginEmailInput, supervisorLoginPasswordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_login);

        mAuth = FirebaseAuth.getInstance();

        supervisorLoginEmailInput = findViewById(R.id.supervisorLoginEmailInput);
        supervisorLoginPasswordInput = findViewById(R.id.supervisorLoginPasswordInput);
        Button supervisorLoginButton = findViewById(R.id.supervisorLoginButton);

        supervisorLoginButton.setOnClickListener(v -> loginSupervisor());
    }

    private void loginSupervisor() {
        String email = supervisorLoginEmailInput.getText().toString().trim();
        String password = supervisorLoginPasswordInput.getText().toString().trim();

        if (email.isEmpty()) {
            supervisorLoginEmailInput.setError("Email is required");
            supervisorLoginEmailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            supervisorLoginPasswordInput.setError("Password is required");
            supervisorLoginPasswordInput.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(supervisorlogin.this, supervisordashboard.class));
                        finish();
                    } else {
                        Toast.makeText(supervisorlogin.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}


