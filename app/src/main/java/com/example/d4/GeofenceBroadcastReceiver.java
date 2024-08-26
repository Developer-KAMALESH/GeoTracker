package com.example.d4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.Geofence;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";
    private FirebaseFirestore db;

    @Override
    public void onReceive(Context context, Intent intent) {
        db = FirebaseFirestore.getInstance();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Handle check-in
            handleCheckIn(context);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Handle check-out
            handleCheckOut(context);
        }
    }

    private void handleCheckIn(Context context) {
        // Save check-in time to Firestore and local storage
        // Example:
        String checkInTime = System.currentTimeMillis() + ""; // Format time as needed
        db.collection("attendance").add(new AttendanceRecord("check_in", checkInTime));
        // Save locally if needed
    }

    private void handleCheckOut(Context context) {
        // Save check-out time to Firestore and local storage
        // Example:
        String checkOutTime = System.currentTimeMillis() + ""; // Format time as needed
        db.collection("attendance").add(new AttendanceRecord("check_out", checkOutTime));
        // Save locally if needed
    }
}
