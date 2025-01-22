package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class UsageReportsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView highestBidTextView, totalBidsTextView, totalPropertiesTextView, totalUsersTextView, mostBidPropertyTextView, approvedUsersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        db = FirebaseFirestore.getInstance();
        highestBidTextView = findViewById(R.id.highestBidTextView);
        totalBidsTextView = findViewById(R.id.totalBidsTextView);
        totalPropertiesTextView = findViewById(R.id.totalPropertiesTextView);
        totalUsersTextView = findViewById(R.id.totalUsersTextView);
        mostBidPropertyTextView = findViewById(R.id.mostBidPropertyTextView);
        approvedUsersTextView = findViewById(R.id.approvedUsersTextView);

        // Fetch the highest bid, counts, and the most bid-on property
        getHighestBid();
        getTotalBidsCount();
        getTotalPropertiesCount();
        getTotalUsersCount();
        getMostBidProperty();
        getApprovedUsersCount();
    }
    private void getHighestBid() {
        db.collection("bids")
                .orderBy("bid_amount", Query.Direction.DESCENDING)
                .limit(1) // Only get the highest bid
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                            // This block will be executed if we have at least one bid
                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                Double highestBid = document.getDouble("bid_amount");

                                if (highestBid != null) {
                                    // Check if bid is 0 and handle separately, if needed
                                    if (highestBid == 0) {
                                        highestBidTextView.setText("Highest Bid: 0 (Minimum bid placed)");
                                    } else {
                                        highestBidTextView.setText("Highest Bid: " + highestBid);
                                    }
                                } else {
                                    highestBidTextView.setText("Error: No valid bid amount found");
                                }
                            }
                        } else {
                            highestBidTextView.setText("No bids available");
                        }
                    } else {
                        highestBidTextView.setText("Error getting the highest bid");
                    }
                });
    }

    private void getTotalBidsCount() {
        db.collection("bids")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            int totalBids = documentSnapshots.size();
                            totalBidsTextView.setText(getString(R.string.total_bids) + totalBids);
                        } else {
                            totalBidsTextView.setText(R.string.no_bids_available);
                        }
                    } else {
                        totalBidsTextView.setText(R.string.error_getting_total_bids_count);
                    }
                });
    }

    private void getTotalPropertiesCount() {
        db.collection("properties")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            int totalProperties = documentSnapshots.size();
                            totalPropertiesTextView.setText("Total Properties: " + totalProperties);
                        } else {
                            totalPropertiesTextView.setText(R.string.no_properties_available);
                        }
                    } else {
                        totalPropertiesTextView.setText(R.string.error_getting_total_properties_count);
                    }
                });
    }

    private void getTotalUsersCount() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            int totalUsers = documentSnapshots.size();
                            totalUsersTextView.setText("Total Users: " + totalUsers);
                        } else {
                            totalUsersTextView.setText("No users available");
                        }
                    } else {
                        totalUsersTextView.setText("Error getting total users count");
                    }
                });
    }

    private void getMostBidProperty() {
        db.collection("bids")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            // Map to store the count of each property_id
                            Map<String, Integer> propertyBidCounts = new HashMap<>();

                            // Iterate over all the bids and count the property_id occurrences
                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                String propertyId = document.getString("property_id");
                                if (propertyId != null) {
                                    propertyBidCounts.put(propertyId, propertyBidCounts.getOrDefault(propertyId, 0) + 1);
                                }
                            }

                            // Find the property with the highest count
                            String mostBidPropertyId = null;
                            int maxBidCount = 0;
                            for (Map.Entry<String, Integer> entry : propertyBidCounts.entrySet()) {
                                if (entry.getValue() > maxBidCount) {
                                    mostBidPropertyId = entry.getKey();
                                    maxBidCount = entry.getValue();
                                }
                            }

                            // Display the most bid-on property
                            if (mostBidPropertyId != null) {
                                mostBidPropertyTextView.setText("Most Bid-on Property ID: " + mostBidPropertyId);
                            } else {
                                mostBidPropertyTextView.setText("No bids available");
                            }
                        } else {
                            mostBidPropertyTextView.setText("Error getting most bid-on property");
                        }
                    }
                });
    }
    private void getApprovedUsersCount() {
        db.collection("users")
                .whereEqualTo("userStatus", "approved") // Filter by 'approved' status
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            int approvedUsersCount = documentSnapshots.size();
                            approvedUsersTextView.setText("Approved Users: " + approvedUsersCount);
                        } else {
                            approvedUsersTextView.setText("No approved users available");
                        }
                    } else {
                        approvedUsersTextView.setText("Error getting approved users count");
                    }
                });
    }

}
