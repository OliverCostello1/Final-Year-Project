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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        int bidderId = sharedPreferences.getInt("user_id", -1); // Replace "user_id" if your key is different
        Log.d("ViewBidsActivity", "Bidder being sent" + bidderId);
        if (bidderId != -1) {
            // API endpoint to fetch bids for the bidder
            String url = "http://10.0.2.2/project/view_bids.php/?bidder_id=" + bidderId;

            // Create a Volley request to fetch the bids
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            progressBar.setVisibility(View.GONE);

                            // Parse the JSON response
                            List<Bid> bids = new ArrayList<>();
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject bidObject = response.getJSONObject(i);
                                    Bid bid = new Bid(
                                            bidObject.getInt("bid_id"),
                                            bidObject.getInt("property_id"),
                                            bidObject.getInt("bidder_id"),
                                            bidObject.getString("bidder_wallet"),
                                            bidObject.getInt("auctioneer_id"),
                                            bidObject.getString("auctioneer_wallet"),
                                            bidObject.getDouble("bid_amount"),
                                            bidObject.getString("time_stamp"),
                                            bidObject.getString("bid_status")
                                    );
                                    bids.add(bid);
                                }

                                if (!bids.isEmpty()) {
                                    bidAdapter.updateData(bids);
                                    bidRecyclerView.setVisibility(View.VISIBLE);
                                    noBidsText.setVisibility(View.GONE);
                                } else {
                                    noBidsText.setVisibility(View.VISIBLE);
                                    bidRecyclerView.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                Log.e("ViewBidsActivity", "Error parsing JSON: " + e.getMessage());
                                noBidsText.setVisibility(View.VISIBLE);
                                bidRecyclerView.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("ViewBidsActivity", "Error fetching bids: " + error.getMessage());
                            noBidsText.setVisibility(View.VISIBLE);
                        }
                    });

            // Add the request to the request queue
            requestQueue.add(jsonArrayRequest);
        } else {
            progressBar.setVisibility(View.GONE);
            noBidsText.setVisibility(View.VISIBLE);
            Log.e("ViewBidsActivity", "Invalid bidder ID.");
        }
    }
}
