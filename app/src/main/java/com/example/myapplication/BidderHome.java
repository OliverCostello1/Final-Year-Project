package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.EventListener;

public class BidderHome extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidder);

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            Toast.makeText(BidderHome.this, "Logging out ... ", Toast.LENGTH_SHORT).show();

            // Return to Main Activity
            Intent intent = new Intent(BidderHome.this, MainActivity.class);

            startActivity(intent);
            finish();
        });
    }
}
