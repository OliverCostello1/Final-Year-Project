package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.firebase.firestore.FirebaseFirestore;

public class BidderActivity extends AppCompatActivity {
    private static final String TAG = "BidderActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidder);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        TextView registerStatus = findViewById(R.id.register_status);
        TextView unapprovedMessage = findViewById(R.id.unapproved_message);
        Button placeBid = findViewById(R.id.place_bid_id);
        Button viewBids = findViewById(R.id.view_bid);
        Button withdrawBids = findViewById(R.id.withdraw_bids);
        Button logout = findViewById(R.id.logout_button);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);

        // Fetch SharedPreferences for user ID
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        String firstName = prefs.getString("first_name", "User");

        // Fetch latest userStatus from Firestore
        if (userId != null) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String status = documentSnapshot.getString("userStatus");
                            if (status == null) status = "pending"; // Default value

                            // Update SharedPreferences with latest status
                            prefs.edit().putString("user_status", status).apply();

                            // Update UI based on status
                            updateUI(status, firstName, registerStatus, unapprovedMessage, placeBid, viewBids, withdrawBids, constraintLayout);

                            Log.d(TAG, "Latest User Status: " + status);
                        } else {
                            Log.e(TAG, "User document not found");
                            finish(); // Close activity if user data is missing
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch userStatus", e);
                        // Fallback to SharedPreferences if Firestore fails
                        String status = prefs.getString("user_status", "pending");
                        updateUI(status, firstName, registerStatus, unapprovedMessage, placeBid, viewBids, withdrawBids, constraintLayout);
                    });
        } else {
            Log.e(TAG, "User ID not found in SharedPreferences");
            finish(); // Close activity if user ID is missing
        }

        // Logout button
        logout.setOnClickListener(v -> {
            startActivity(new Intent(BidderActivity.this, MainActivity.class));
            finish();
        });

        // Navigation button listeners
        placeBid.setOnClickListener(v -> startActivity(new Intent(BidderActivity.this, PlaceBidActivity.class)));
        viewBids.setOnClickListener(v -> startActivity(new Intent(BidderActivity.this, ViewBidsActivity.class)));
        withdrawBids.setOnClickListener(v -> startActivity(new Intent(BidderActivity.this, WithdrawBidsActivity.class)));
    }

    private void updateUI(String status, String firstName, TextView registerStatus, TextView unapprovedMessage,
                          Button placeBid, Button viewBids, Button withdrawBids, ConstraintLayout constraintLayout) {
        registerStatus.setText(getString(R.string.register_status, status));

        if ("pending".equals(status)) {
            unapprovedMessage.setVisibility(View.VISIBLE);
            registerStatus.setVisibility(View.VISIBLE);
            placeBid.setVisibility(View.GONE);
            viewBids.setVisibility(View.GONE);
            withdrawBids.setVisibility(View.GONE);
        } else if ("approved".equals(status)) {
            unapprovedMessage.setVisibility(View.VISIBLE);
            unapprovedMessage.setText(getString(R.string.welcome_back_string, firstName));
            registerStatus.setVisibility(View.GONE);
            placeBid.setVisibility(View.VISIBLE);
            viewBids.setVisibility(View.VISIBLE);
            withdrawBids.setVisibility(View.VISIBLE);
        }

        // Update constraints if buttons are hidden
        if (placeBid.getVisibility() == View.GONE && viewBids.getVisibility() == View.GONE && withdrawBids.getVisibility() == View.GONE) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.logout_button, ConstraintSet.TOP, R.id.unapproved_message, ConstraintSet.BOTTOM, 32);
            constraintSet.applyTo(constraintLayout);
        }

        Log.d(TAG, "WithdrawBids Button Visibility: " + withdrawBids.getVisibility());
    }
}