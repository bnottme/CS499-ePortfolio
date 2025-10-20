package com.example.projectonebrettn;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


// Source code: https://stackoverflow.com/questions/44305206/ask-permission-for-push-notification

// This screen asks the user if they want to allow notifications
public class SmsAgreement extends AppCompatActivity {

    // Track whether the user gave notification permission
    private boolean hasNotificationPermissionGranted = false;

    // Launcher that handles requesting the notification permission
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                hasNotificationPermissionGranted = isGranted;
                if (isGranted) {
                    // If user says yes show success message and send notification
                    Toast.makeText(getApplicationContext(), "Notification permission granted", Toast.LENGTH_SHORT).show();
                    showNotification();
                } else {
                    // If denied  check version and either show rationale or send to settings
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= 33) {
                            if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                                showNotificationPermissionRationale();
                            } else {
                                showSettingDialog();
                            }
                        }
                    }
                }
            });

    // Show a dialog asking the user to enable permission in phone settings
    private void showSettingDialog() {
        new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle("Notification Permission")
                .setMessage("Notification permission is required, Please allow notification permission from settings.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Open the app’s settings page so the user can manually allow notifications
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Show dialog explaining why notifications are needed
    private void showNotificationPermissionRationale() {
        new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle("Alert")
                .setMessage("Notification permission is required to show notifications.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Ask again if user agrees
                    if (Build.VERSION.SDK_INT >= 33) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_agreement);

        // Handle area padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Yes button ask for notification permission and go to event list
        Button yesButton = findViewById(R.id.sms_button_yes);
        yesButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 33) {
                // Ask for runtime permission on Android 13+
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            } else {
                // Older Android permission not required
                hasNotificationPermissionGranted = true;
                showNotification();
            }

            // Move to event list screen
            startActivity(new Intent(SmsAgreement.this, data_grid.class));
        });

        // No button just skip notifications and go to event list
        Button noButton = findViewById(R.id.sms_button_no);
        noButton.setOnClickListener(v -> {
            startActivity(new Intent(SmsAgreement.this, data_grid.class));
        });
    }

    // Build and display a notification
    private void showNotification() {
        String channelId = "12345";
        String description = "Test Notification";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8+, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Build the actual notification message
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Welcome to Event Tracker!") // Title
                .setContentText("You have signed up for notifications") // Message body
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Small app icon
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_background)); // Large icon

        // Show the notification
        notificationManager.notify(12345, builder.build());
    }
}