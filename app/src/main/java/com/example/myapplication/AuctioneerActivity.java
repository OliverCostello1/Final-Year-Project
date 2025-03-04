package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuctioneerActivity extends AppCompatActivity {
    private static final String TAG = "AuctioneerActivity";
    private Button approveBid, addNewProperty, viewBids, logout;
    private TextView unapprovedMessage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctioneers);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        approveBid = findViewById(R.id.approve_bid);
        addNewProperty = findViewById(R.id.add_new_property);
        viewBids = findViewById(R.id.view_bids);
        logout = findViewById(R.id.logout);
        unapprovedMessage = findViewById(R.id.unapproved_message); // Add this TextView in your XML

        // Check user status and update UI
        checkUserStatus();

        // Set button click listeners
        approveBid.setOnClickListener(v -> {
            Intent intent = new Intent(AuctioneerActivity.this, ApproveBidsActivity.class);
            startActivity(intent);
        });

        addNewProperty.setOnClickListener(v -> {
            Intent intent = new Intent(AuctioneerActivity.this, AddPropertyActivity.class);
            startActivity(intent);
        });

        viewBids.setOnClickListener(v -> {
            Intent intent = new Intent(AuctioneerActivity.this, ViewBidsActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            Intent intent = new Intent(AuctioneerActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        });
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No user is logged in");
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userStatus = documentSnapshot.getString("userStatus");
                        Log.d(TAG, "User status: " + userStatus);

                        if ("pending".equalsIgnoreCase(userStatus)) {
                            // Show unapproved message and disable buttons
                            unapprovedMessage.setVisibility(View.VISIBLE);
                            approveBid.setEnabled(false);
                            addNewProperty.setEnabled(false);
                            viewBids.setEnabled(false);
                            Log.d(TAG, "User is pending approval, buttons disabled");
                        } else {
                            // Hide message and ensure buttons are enabled
                            unapprovedMessage.setVisibility(View.GONE);
                            approveBid.setEnabled(true);
                            addNewProperty.setEnabled(true);
                            viewBids.setEnabled(true);
                            Log.d(TAG, "User is approved, buttons enabled");
                        }
                    } else {
                        Log.e(TAG, "User document not found for ID: " + userId);
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user status", e);
                    Toast.makeText(this, "Error checking user status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}