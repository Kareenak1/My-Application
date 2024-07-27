package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;
import java.util.regex.Pattern;

public class supervisorregister extends AppCompatActivity {

    private EditText supervisorEmailInput, supervisorNameInput, supervisorPasswordInput, supervisorConfirmPasswordInput;
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
        setContentView(R.layout.activity_supervisor_registration);

        mAuth = FirebaseAuth.getInstance();

        supervisorEmailInput = findViewById(R.id.supervisorEmailInput);
        supervisorNameInput = findViewById(R.id.supervisorNameInput);
        supervisorPasswordInput = findViewById(R.id.supervisorPasswordInput);
        supervisorConfirmPasswordInput = findViewById(R.id.supervisorConfirmPasswordInput);
        Button supervisorRegisterButton = findViewById(R.id.supervisorRegisterButton);

        supervisorRegisterButton.setOnClickListener(v -> registerSupervisor());
    }

    private void registerSupervisor() {
        String email = supervisorEmailInput.getText().toString().trim();
        String name = supervisorNameInput.getText().toString().trim();
        String password = supervisorPasswordInput.getText().toString().trim();
        String confirmPassword = supervisorConfirmPasswordInput.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("dkut.ac.ke")) {
            supervisorEmailInput.setError("Invalid email format");
            supervisorEmailInput.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            supervisorNameInput.setError("Name is required");
            supervisorNameInput.requestFocus();
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            supervisorPasswordInput.setError("Password must be at least 8 characters long, contain at least 2 letters, and 2 symbols");
            supervisorPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            supervisorConfirmPasswordInput.setError("Passwords do not match");
            supervisorConfirmPasswordInput.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(supervisorregister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(supervisorregister.this, supervisorlogin.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(supervisorregister.this, "Error updating profile: " + Objects.requireNonNull(updateTask.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(supervisorregister.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}