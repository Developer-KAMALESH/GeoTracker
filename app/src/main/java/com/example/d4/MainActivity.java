package com.example.d4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d4.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import android.app.PendingIntent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private TextView statusTextView;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private GeofencingClient geofencingClient;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        textView = findViewById(R.id.textView);
        statusTextView = findViewById(R.id.statusTextView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call the method to refresh content
                refreshContent();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        FirebaseApp.initializeApp(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            setupGeofencing();
        }

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, 2404).show();
        } else {
            // Proceed with geofencing setup
            setupGeofencing();
        }

        // Store check-in and check-out times for demonstration
        storeCheckInTimeFirestore();
        storeCheckOutTimeFirestore();
    }

    private void refreshContent() {
        // Simulate refreshing content (e.g., update text or fetch new data)
        textView.setText("Refreshing content...");

        // Simulate a delay for refreshing (e.g., network request)
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update TextView to show new content
                textView.setText("Content refreshed!");

                // Notify SwipeRefreshLayout that refresh has finished
                swipeRefreshLayout.setRefreshing(false);

                // Fade out the "Attendance status updated!" text after refreshing
                fadeOutText("Attendance status updated!");
            }
        }, 2000); // 2 seconds delay
    }

    private void setupGeofencing() {
        // Define a geofence
        Geofence geofence = new Geofence.Builder()
                .setRequestId("GeofenceId")
                .setCircularRegion(13.334049, 79.891169, 200) // Example coordinates and radius
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                // Prompt the user to disable battery optimization for your app
                Intent batteryOptimizationIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(batteryOptimizationIntent);
            }
        }

        // Add the geofence
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(this, aVoid -> statusTextView.setText("Geofencing setup successfully."))
                .addOnFailureListener(this, e -> statusTextView.setText("Geofencing setup failed: " + e.getMessage()));
    }

    private void storeCheckInTimeFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String username = user.getUid();
            Map<String, Object> checkInData = new HashMap<>();
            checkInData.put("checkInTime", getCurrentTime());

            db.collection("employees").document(username)
                    .set(checkInData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Check-in time saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error saving check-in time", Toast.LENGTH_SHORT).show());
        }
    }

    private void storeCheckOutTimeFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String username = user.getUid();
            Map<String, Object> checkOutData = new HashMap<>();
            checkOutData.put("checkOutTime", getCurrentTime());

            db.collection("employees").document(username)
                    .set(checkOutData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Check-out time saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error saving check-out time", Toast.LENGTH_SHORT).show());
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void fadeOutText(final String text) {
        textView.setText(text);
        textView.animate().alpha(0).setDuration(500).withEndAction(() -> {
            textView.setVisibility(TextView.GONE);
            // Update text to show the actual status after fading out
            textView.setText(getGeofencedStatus());
            textView.setVisibility(TextView.VISIBLE);
            textView.animate().alpha(1).setDuration(500);
        });
    }

    private String getGeofencedStatus() {
        // Fetch and return the current geofenced status
        return "Geofenced status updated"; // Placeholder for actual status
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupGeofencing();
            } else {
                statusTextView.setText("Location permission denied.");
            }
        }
    }
}
