package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;

public class studentdashboard extends AppCompatActivity {
    private static final String TAG = "StudentDashboard";

    private FirebaseUser user;
    private StorageReference storageReference;

    private TextView greetingsText;
    private ImageView profilePlaceholder;
    private ViewSwitcher viewSwitcher;
    private LinearLayout navHome, navWorkspace, navSettings;
    private ImageView iconHome, iconWorkspace, iconSettings;
    private TextView labelHome, labelWorkspace, labelSettings;

    private ActivityResultLauncher<Intent> uploadImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        greetingsText = findViewById(R.id.greetings_text);
        profilePlaceholder = findViewById(R.id.profile_placeholder);
        viewSwitcher = findViewById(R.id.view_switcher);

        navHome = findViewById(R.id.nav_home);
        navWorkspace = findViewById(R.id.nav_workspace);
        navSettings = findViewById(R.id.nav_settings);

        iconHome = findViewById(R.id.icon_home);
        iconWorkspace = findViewById(R.id.icon_workspace);
        iconSettings = findViewById(R.id.icon_settings);

        labelHome = findViewById(R.id.label_home);
        labelWorkspace = findViewById(R.id.label_workspace);
        labelSettings = findViewById(R.id.label_settings);

        setGreetingsText();
        setupProfilePlaceholder();
        setupUploadImageLauncher();

        navHome.setOnClickListener(v -> navigateToHome());
        navWorkspace.setOnClickListener(v -> navigateToWorkspace());
        navSettings.setOnClickListener(v -> navigateToSettings());

        findViewById(R.id.groups_nav).setOnClickListener(v -> showGroupsPage());
        findViewById(R.id.tasks_nav).setOnClickListener(v -> showTasksPage());
    }

    private void setGreetingsText() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good morning, " + (user != null ? user.getDisplayName() : "User");
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good afternoon, " + (user != null ? user.getDisplayName() : "User");
        } else {
            greeting = "Good evening, " + (user != null ? user.getDisplayName() : "User");
        }
        greetingsText.setText(greeting);
    }

    private void setupProfilePlaceholder() {
        profilePlaceholder.setOnClickListener(v -> openImageSelector());

        if (user != null && user.getPhotoUrl() != null) {
            profilePlaceholder.setImageURI(user.getPhotoUrl());
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        uploadImageLauncher.launch(intent);
    }

    private void setupUploadImageLauncher() {
        uploadImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                uploadImage(selectedImageUri);
                            }
                        }
                    }
                }
        );
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child("users/" + user.getUid() + "/profile.jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        profilePlaceholder.setImageURI(uri);
                                        Toast.makeText(studentdashboard.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload image: " + e.getMessage());
                        Toast.makeText(studentdashboard.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void navigateToHome() {
        resetNavIcons();
        iconHome.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
        labelHome.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        // Add logic to navigate to the Home section
    }

    private void navigateToWorkspace() {
        resetNavIcons();
        iconWorkspace.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
        labelWorkspace.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        // Add logic to navigate to the Workspace section
    }

    private void navigateToSettings() {
        resetNavIcons();
        iconSettings.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
        labelSettings.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        // Add logic to navigate to the Settings section
    }

    private void resetNavIcons() {
        iconHome.setColorFilter(getResources().getColor(android.R.color.black));
        iconWorkspace.setColorFilter(getResources().getColor(android.R.color.black));
        iconSettings.setColorFilter(getResources().getColor(android.R.color.black));

        labelHome.setTextColor(getResources().getColor(android.R.color.black));
        labelWorkspace.setTextColor(getResources().getColor(android.R.color.black));
        labelSettings.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void showGroupsPage() {
        viewSwitcher.setDisplayedChild(0);
    }

    private void showTasksPage() {
        viewSwitcher.setDisplayedChild(1);
    }
}
