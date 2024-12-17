package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BidderActivity extends AppCompatActivity {
    private static final String TAG = "BidderActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidder);

        // Initialize UI components
        TextView registerStatus = findViewById(R.id.register_status);
        TextView unapprovedMessage = findViewById(R.id.unapproved_message);
        Button placeBid = findViewById(R.id.place_bid_id);
        Button viewBids = findViewById(R.id.view_bid);
        TextView welcomeTextView = findViewById(R.id.auctioneer_home);
        Button logout =  findViewById(R.id.logout_button);
        // Fetch SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String status = prefs.getString("user_status", "");
        String firstName = prefs.getString("first_name", "");

        // Set register status message
        registerStatus.setText(getString(R.string.register_status, status));

        logout.setOnClickListener(v -> {
            startActivity(new Intent(BidderActivity.this, MainActivity.class));
        });

        // Handling visibility based on user status
        if ("pending".equals(status)) {
            unapprovedMessage.setVisibility(View.VISIBLE);
            registerStatus.setVisibility(View.VISIBLE);
            placeBid.setVisibility(View.GONE);
            viewBids.setVisibility(View.GONE);
            welcomeTextView.setVisibility(View.GONE);
        } else if ("approved".equals(status)) {
            unapprovedMessage.setVisibility(View.GONE);
            registerStatus.setVisibility(View.GONE);
            placeBid.setVisibility(View.VISIBLE);
            viewBids.setVisibility(View.VISIBLE);
            welcomeTextView.setVisibility(View.VISIBLE);
            welcomeTextView.setText(getString(R.string.welcome_back_string, firstName));
        }

        // Button to navigate to PlaceBidActivity
        placeBid.setOnClickListener(v -> {
            Intent intent = new Intent(BidderActivity.this, PlaceBidActivity.class);
            startActivity(intent);
        });

        // Button to navigate to ViewBidsActivity
        viewBids.setOnClickListener(v -> {
            Intent intent = new Intent(BidderActivity.this, ViewBidsActivity.class);
            startActivity(intent);
        });
    }
}
