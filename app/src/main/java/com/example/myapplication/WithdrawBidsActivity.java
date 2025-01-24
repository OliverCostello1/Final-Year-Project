package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class WithdrawBidsActivity extends AppCompatActivity {
    private static final String TAG = "WithdrawBidsActivity";

    private RecyclerView bidRecyclerView;
    private BidAdapter bidAdapter;
    private ProgressBar progressBar;
    private TextView noBidsText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_bids);

        // Initialize UI components
        bidRecyclerView = findViewById(R.id.bidRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noBidsText = findViewById(R.id.noBidsText);

        // Setup RecyclerView
        bidRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bidAdapter = new BidAdapter(new ArrayList<>(), this, this::withdrawBid);
        bidRecyclerView.setAdapter(bidAdapter);

        // Load bids for the logged-in bidder
        loadBids();
    }

    private void loadBids() {
        progressBar.setVisibility(View.VISIBLE);

        // Get the logged-in user's ID
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        if (!userId.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference bidsRef = db.collection("bids");

            // Query Firestore for the bids of the logged-in bidder
            Query query = bidsRef.whereEqualTo("bidder_id", userId);

            query.get().addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Bid> bids = new ArrayList<>();

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Bid bid = document.toObject(Bid.class);
                            if (bid != null) {
                                bids.add(bid);
                            }
                        }

                        if (!bids.isEmpty()) {
                            bidAdapter.updateData(bids);
                            bidRecyclerView.setVisibility(View.VISIBLE);
                            noBidsText.setVisibility(View.GONE);
                        } else {
                            noBidsText.setVisibility(View.VISIBLE);
                            bidRecyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        noBidsText.setVisibility(View.VISIBLE);
                        bidRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Error fetching bids: " + task.getException().getMessage());
                    noBidsText.setVisibility(View.VISIBLE);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            noBidsText.setVisibility(View.VISIBLE);
            Log.e(TAG, "Invalid user ID.");
        }
    }

    private void withdrawBid(String bidId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bidsRef = db.collection("bids");

        // Update the bid's status to 'withdrawn' and set 'contract_generated' to false
        bidsRef.document(bidId)
                .update("bid_status", "withdrawn", "contract_generated", false)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Bid withdrawn successfully: " + bidId);
                    Toast.makeText(this, "Bid withdrawn successfully.", Toast.LENGTH_SHORT).show();
                    // Refresh the list after withdrawal
                    loadBids();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error withdrawing bid: " + e.getMessage());
                    Toast.makeText(this, "Failed to withdraw bid.", Toast.LENGTH_SHORT).show();
                });
    }
}
