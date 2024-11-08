package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import jnr.ffi.annotations.In;

public class BidderActivity extends AppCompatActivity {
    private static final String TAG = "BidderActivity";
    private Button viewBids;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidder);
        TextView registerStatus = findViewById(R.id.register_status);
        TextView unapprovedMessage = findViewById(R.id.unapproved_message);
        Button placeBid = findViewById(R.id.place_bid_id);
        viewBids = findViewById(R.id.view_bid);
        TextView welcomeTextView = findViewById(R.id.welcome_back);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String status = prefs.getString("user_status", "");
        registerStatus.setText(getString(R.string.register_status, status));
        String first_name = prefs.getString("first_name", "");
        if ("pending".equals(status)) {
            unapprovedMessage.setVisibility(TextView.VISIBLE);
        } else if ("approved".equals(status)) {
            registerStatus.setVisibility(TextView.GONE);
            placeBid.setVisibility(Button.VISIBLE);
            viewBids.setVisibility(Button.VISIBLE);
            welcomeTextView.setVisibility(TextView.VISIBLE);
            welcomeTextView.setText(getString(R.string.welcome_back_string, first_name));
        }

        // If the place bids button is visible allow user to go to place bids activity
        if (placeBid.getVisibility() == Button.VISIBLE) {
           placeBid.setOnClickListener(v -> {
              Intent intent = new Intent(BidderActivity.this, PlaceBidActivity.class);
              startActivity(intent);
           });
        }

        // If the place bids button is visible, allow user to view their bids.

        if (viewBids.getVisibility() == Button.VISIBLE) {
            viewBids.setOnClickListener(v -> {
                Intent intent = new Intent(BidderActivity.this, ViewBidsActivity.class);
                startActivity(intent);
            });
        }
    }
}
