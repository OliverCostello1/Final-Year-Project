package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlaceBidActivity extends AppCompatActivity {
    private static final String TAG = "PlaceBidActivity";
    private PropertyAdapter propertyAdapter;
    private RecyclerView propertyRecyclerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_bid);

        // Initialize RecyclerView
        propertyRecyclerView = findViewById(R.id.propertyRecycler);
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list
        propertyAdapter = new PropertyAdapter(new ArrayList<>(), this);
        propertyRecyclerView.setAdapter(propertyAdapter);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Fetch properties from Firestore
        fetchProperties();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void fetchProperties() {
        CollectionReference propertiesRef = db.collection("properties");

        // Fetch properties from Firestore
        propertiesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Property> properties = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Property property = document.toObject(Property.class);
                    properties.add(property);
                    Log.d(TAG, "Fetched property: " + property.getPropertyId());
                }
                propertyAdapter.updateData(properties);
                propertyRecyclerView.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "Error fetching properties", task.getException());
                Toast.makeText(PlaceBidActivity.this, "Failed to fetch properties", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submitBid(Property property, String userId, String userWallet, double bidAmount) {

        // This will be used to only allow bidder to submit a bid during business hours (Mon-Fri, 9am-5pm)
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY && hourOfDay >=9 && hourOfDay <= 17) {
            // Generate bid ID
            String bidId = db.collection("bids").document().getId(); // Automatically generate unique ID

            // Prepare bid data
            Bid bid = new Bid(
                    bidId,
                    String.valueOf(property.getPropertyId()),  // Assuming propertyId is string and needs conversion
                    String.valueOf(userId), // Assuming userId is integer
                    userWallet,
                    String.valueOf(property.getAuctioneer_id()), // Assuming auctioneer_id is integer
                    property.getAuctioneer_wallet(),
                    bidAmount,
                    String.valueOf(System.currentTimeMillis()),  // Use current timestamp
                    "pending" // Initially set bid status to "pending"
            );

            // Submit bid to Firestore
            CollectionReference bidsRef = db.collection("bids");
            bidsRef.add(bid)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(PlaceBidActivity.this, "Bid placed successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Bid placed with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PlaceBidActivity.this, "Failed to submit bid: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error submitting bid", e);
                    });
        } else {
            Toast.makeText(PlaceBidActivity.this, "Bids can only be submitted between Monday and Friday, 9 AM to 5 PM.", Toast.LENGTH_SHORT).show();
        }
    }
}
