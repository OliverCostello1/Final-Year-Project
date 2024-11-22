// Main page for auctioneers when they successfully log in.
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AuctioneerActivity extends AppCompatActivity {
    public Button approve_bid, add_new_property;
    public Button view_bids;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctioneers);
        approve_bid = findViewById(R.id.approve_bid);
        add_new_property =  findViewById(R.id.add_new_property);
        view_bids = findViewById(R.id.view_bids);
        approve_bid.setOnClickListener(v -> {
           Intent intent = new Intent(AuctioneerActivity.this, ApproveBidsActivity.class );
           startActivity(intent);
        });

        add_new_property.setOnClickListener(v -> {
            Intent intent = new Intent(AuctioneerActivity.this, AddPropertyActivity.class);
            startActivity(intent);
        });

        view_bids.setOnClickListener(v -> {
            Intent intent = new Intent(AuctioneerActivity.this, ViewBidsActivity.class);
            startActivity(intent);
        });

    }


}
