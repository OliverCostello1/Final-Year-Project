package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApproveBidsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApproveBidsAdapter adapter;
    private List<Bid> bids;
    private Button return_button;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_bids);

        recyclerView = findViewById(R.id.activity_approve_bids_recycler);
        return_button = findViewById(R.id.return_button);

        return_button.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        if (recyclerView != null) {
            bids = new ArrayList<>();
            adapter = new ApproveBidsAdapter(bids, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);  // Set your adapter
        } else {
            Log.e("ApproveBidsActivity", "RecyclerView is null!");
        }

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int auctioneerID = sharedPreferences.getInt("user_id", -1);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Fetch pending bids
        fetchPendingBids(auctioneerID);
    }

    private void fetchPendingBids(int auctioneerID) {
        // Query Firestore for bids with bid_status = "pending"
        db.collection("bids")
                .whereEqualTo("bid_status", "pending")
                .whereEqualTo("auctioneer_id", auctioneerID) // Ensure you filter by auctioneer ID
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        bids.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Bid bid = document.toObject(Bid.class);
                            bids.add(bid);
                        }

                        // Logging the updated bids list for debugging
                        Log.d("ApproveBidsActivity", "Updated bids list: " + bids);

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("ApproveBidsActivity", "No pending bids found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ApproveBidsActivity", "Error fetching bids: " + e.getMessage());
                    Toast.makeText(ApproveBidsActivity.this, "Error fetching bids: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void approveBid(int bidId) {
        // Find the bid and update its status
        for (Bid bid : bids) {
            if (bid.getBid_id() == bidId) {
                // Update the bid status to "approved"
                db.collection("bids").document(String.valueOf(bidId))
                        .update("bid_status", "approved")
                        .addOnSuccessListener(aVoid -> {
                            bid.setBidStatus("approved");
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ApproveBidsActivity.this, "Bid approved successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ApproveBidsActivity", "Error approving bid: " + e.getMessage());
                            Toast.makeText(ApproveBidsActivity.this, "Error approving bid: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                break;
            }
        }
    }
}
