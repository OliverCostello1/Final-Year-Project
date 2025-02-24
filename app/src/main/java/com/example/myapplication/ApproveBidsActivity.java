package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveBidsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApproveBidsAdapter adapter;
    private List<Bid> bids;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_bids);

        recyclerView = findViewById(R.id.activity_approve_bids_recycler);
        if (recyclerView != null) {
            bids = new ArrayList<>();
            adapter = new ApproveBidsAdapter(bids, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);  // Set your adapter
            // Log initial empty state of bids
            Log.d("ApproveBidsActivity", "Initial bids list: " + bids.toString());
        } else {
            Log.e("ApproveBidsActivity", "RecyclerView is null!");
        }

        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String auctioneerID = sharedPreferences.getString("user_id", ""); // Assuming user_id is stored as String
        Log.d("ApproveBidsActivity", "User ID: " + auctioneerID);
        fetchPendingBids(auctioneerID);



        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }

    private void fetchPendingBids(String auctioneerID) {
        CollectionReference bidsCollection = firestore.collection("bids");

        // Query for pending bids for the current auctioneer
        Query query = bidsCollection
                .whereEqualTo("auctioneer_id", auctioneerID)
                .whereEqualTo("bid_status", "pending");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    bids.clear();
                    // Loop through each document in the query result
                    for (DocumentSnapshot document : querySnapshot) {
                        Log.d("ApproveBidsActivity", "Document ID: " + document.getId());
                        Log.d("ApproveBidsActivity", "Bid Data: " + document.getData());

                        // Deserialize the document into a Bid object
                        Bid bid = document.toObject(Bid.class);
                        if (bid != null) {
                            Log.d("ApproveBidsActivity", "Bid: " + bid.toString());
                            bids.add(bid);
                        } else {
                            Log.d("ApproveBidsActivity", "Bid object is null");
                        }
                    }
                    Log.d("ApproveBidsActivity", "Updated bids list: " + bids.toString());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ApproveBidsActivity", "No pending bids found for auctioneer ID: " + auctioneerID);
                }
            } else {
                Log.e("ApproveBidsActivity", "Error fetching bids: " + task.getException());
                Toast.makeText(ApproveBidsActivity.this, "Error fetching bids", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void approveBid(String bidId) {
        DocumentReference bidRef = firestore.collection("bids").document(bidId);

        bidRef.update("bid_status", "approved")
                .addOnSuccessListener(aVoid -> {
                    // Log the current bids list and compare the bidId
                    Log.d("ApproveBidsActivity", "Bid approved with ID: " + bidId);
                    boolean bidFound = false;
                    for (Bid bid : bids) {
                        if (bid.getBid_id().equals(bidId)) {
                            bid.setBidStatus("approved");
                            bidFound = true;
                            break;
                        }
                    }
                    if (bidFound) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ApproveBidsActivity.this, "Bid approved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ApproveBidsActivity", "Approval failed: Bid not found or already approved");
                        Toast.makeText(ApproveBidsActivity.this, "Bid not found or already approved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ApproveBidsActivity", "Error approving bid: " + e.getMessage());
                    Toast.makeText(ApproveBidsActivity.this, "Error approving bid", Toast.LENGTH_SHORT).show();
                });
    }

    public void denyBid(String bidId) {
        // Create an AlertDialog with an input field for the denial reason
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deny Bid");

        // Create an EditText input field
        final EditText input = new EditText(this);
        input.setHint("Enter reason for denial (optional)");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Deny", (dialog, which) -> {
            String description = input.getText().toString().trim(); // Get user input
            if (description.isEmpty()) {
                description = ""; // Default to empty string if no input
            }

            DocumentReference bidRef = firestore.collection("bids").document(bidId);

            // Prepare data to update Firestore
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("bid_status", "denied");
            updateData.put("bid_description", description);

            bidRef.update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ApproveBidsActivity", "Bid denied with ID: " + bidId);
                        boolean bidFound = false;
                        for (Bid bid : bids) {
                            if (bid.getBid_id().equals(bidId)) {
                                bid.setBidStatus("denied");
                                bidFound = true;
                                break;
                            }
                        }
                        if (bidFound) {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ApproveBidsActivity.this, "Bid denied successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ApproveBidsActivity", "Denial failed: Bid not found or already denied");
                            Toast.makeText(ApproveBidsActivity.this, "Bid not found or already denied", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ApproveBidsActivity", "Error denying bid: " + e.getMessage());
                        Toast.makeText(ApproveBidsActivity.this, "Error denying bid", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}

