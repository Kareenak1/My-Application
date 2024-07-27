package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(v -> checkIfRegistered());
    }

    private void checkIfRegistered() {
        boolean isSupRegistered = isSupervisorRegistered();
        boolean isStudentRegistered = isStudentRegistered();

        if (isSupRegistered) {
            startActivity(new Intent(main.this, supervisorlogin.class));
        } else if (isStudentRegistered) {
            startActivity(new Intent(main.this, studentlogin.class));
        } else {
            showRoleSelectionDialog();
        }
    }

    private void showRoleSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Role")
                .setItems(new CharSequence[]{"Student", "Supervisor"}, (dialog, which) -> {
                    if (which == 0) {
                        startActivity(new Intent(main.this, studentregistration.class));
                    } else {
                        startActivity(new Intent(main.this, supervisorregister.class));
                    }
                })
                .show();
    }

    private boolean isSupervisorRegistered() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("is_sup_registered", false);
    }

    private boolean isStudentRegistered() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("is_student_registered", false);
    }

    // Call this method when the user registers successfully
    public void setRegistered(boolean isSupervisor) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (isSupervisor) {
            editor.putBoolean("is_sup_registered", true);
        } else {
            editor.putBoolean("is_student_registered", true);
        }
        editor.apply();
    }
}
