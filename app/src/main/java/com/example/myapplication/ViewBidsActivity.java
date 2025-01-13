package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewBidsActivity extends AppCompatActivity {

    private RecyclerView bidRecyclerView;
    private BidAdapter bidAdapter;
    private ProgressBar progressBar;
    private TextView noBidsText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bids);

        // Initialize UI components
        bidRecyclerView = findViewById(R.id.bidRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noBidsText = findViewById(R.id.noBidsText);

        // Setup RecyclerView
        bidRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bidAdapter = new BidAdapter(new ArrayList<>(), this);
        bidRecyclerView.setAdapter(bidAdapter);

        // Load bids for the logged-in bidder
        loadBids();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void loadBids() {
        // Show progress bar while loading
        progressBar.setVisibility(View.VISIBLE);

        // Fetch the logged-in user's bidder ID
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String bidderId = sharedPreferences.getString("user_id", ""); // Replace "user_id" if your key is different
        Log.d("ViewBidsActivity", "Bidder being sent" + bidderId);

        if (!bidderId.isEmpty()){
            // Get Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference bidsRef = db.collection("bids");

            // Query Firestore for the bids belonging to the logged-in bidder
            Query query = bidsRef.whereEqualTo("bidder_id", bidderId);

            // Fetch the data from Firestore
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    QuerySnapshot querySnapshot = task.getResult();
                    List<Bid> bids = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Bid bid = document.toObject(Bid.class); // Assuming Bid class is setup for Firestore
                            if (bid != null) {
                                // Format the timestamp here before adding to the list
                                String formattedTimeStamp = formatTimeStamp(bid.getTime_stamp());
                                bid.setFormattedTimeStamp(formattedTimeStamp); // Set the formatted timestamp in the Bid
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
                    progressBar.setVisibility(View.GONE);
                    Log.e("ViewBidsActivity", "Error fetching bids: " + task.getException().getMessage());
                    noBidsText.setVisibility(View.VISIBLE);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            noBidsText.setVisibility(View.VISIBLE);
            Log.e("ViewBidsActivity", "Invalid bidder ID.");
        }
    }
    private String formatTimeStamp(String timeStamp) {
        try {
            // Assuming time_stamp is a Unix timestamp in milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date(Long.parseLong(timeStamp)); // Parse timestamp to Date object
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }


}
