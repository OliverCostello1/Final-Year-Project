package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
        Button withdrawBids = findViewById(R.id.withdraw_bids);
        Button logout =  findViewById(R.id.logout_button);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);


        // Fetch SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String status = prefs.getString("userStatus", "");
        String firstName = prefs.getString("firstName", "");
        Log.d("BidderActivity"+ "first name: " , firstName);
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
            withdrawBids.setVisibility(View.GONE);
        } else if ("approved".equals(status)) {
            unapprovedMessage.setVisibility(View.GONE);
            registerStatus.setVisibility(View.GONE);
            placeBid.setVisibility(View.VISIBLE);
            viewBids.setVisibility(View.VISIBLE);
            withdrawBids.setVisibility(View.VISIBLE);
            unapprovedMessage.setVisibility(View.VISIBLE);
            unapprovedMessage.setText(getString(R.string.welcome_back_string, firstName));
        }



        Log.d(TAG, "WithdrawBids Button Visibility: " + withdrawBids.getVisibility());
        Log.d(TAG, "User Status: " + status);
        // Update constraints upon visibility change for user experience
        if (placeBid.getVisibility() == View.GONE && viewBids.getVisibility() == View.GONE && withdrawBids.getVisibility() == View.GONE) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(
                    R.id.logout_button,
                    ConstraintSet.TOP,
                    R.id.unapproved_message,
                    ConstraintSet.BOTTOM,
                    32
            );
            constraintSet.applyTo(constraintLayout);
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
        // Button to navigate to WithdrawBidsActivity
        withdrawBids.setOnClickListener(v -> {
            Intent intent = new Intent(BidderActivity.this, WithdrawBidsActivity.class);
            startActivity(intent);
        });
    }
}
