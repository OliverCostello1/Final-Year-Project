package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.datatypes.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApproveBidsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApproveBidsAdapter adapter;
    private List<Bid> bids;
    private SharedPreferences sharedPreferences;
    private static final String GET_BIDS_URL = "http://10.0.2.2:8000/project/get_pending_bids.php";
    private static final String APPROVE_BID_URL = "http://10.0.2.2:8000/project/approve_bids.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_bids);

        recyclerView = findViewById(R.id.activity_approve_bids_recycler);


        if (recyclerView != null) {
            bids = new ArrayList<>();
            adapter  = new ApproveBidsAdapter(bids ,this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);  // Set your adapter

            // Log initial empty state of bids
            Log.d("ApproveBidsActivity", "Initial bids list: " + bids.toString());


        } else {
            Log.e("ApproveBidsActivity", "RecyclerView is null!");
        }
        bids = new ArrayList<>();
        adapter = new ApproveBidsAdapter(bids, this);
        recyclerView.setAdapter(adapter);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int auctioneerID = sharedPreferences.getInt("user_id", -1);

        fetchPendingBids(auctioneerID);

    }
    private void fetchPendingBids(int auctioneerID) {
        String url = GET_BIDS_URL + "?auctioneer_id=" + auctioneerID;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Log the raw response
                            Log.d("ApproveBidsActivity", "Raw API Response: " + response.toString());

                            // Check if the status is success
                            if (response.getString("status").equals("success")) {
                                JSONArray bidsArray = response.getJSONArray("bids");
                                bids.clear();

                                // Populate the bids list
                                for (int i = 0; i < bidsArray.length(); i++) {
                                    JSONObject bidJson = bidsArray.getJSONObject(i);
                                    bids.add(new Bid(
                                            bidJson.getInt("bid_id"),
                                            bidJson.getInt("property_id"),
                                            bidJson.getInt("bidder_id"),
                                            bidJson.getString("bidder_wallet"),
                                            bidJson.getInt("auctioneer_id"),
                                            bidJson.getString("auctioneer_wallet"),
                                            bidJson.getDouble("bid_amount"),
                                            bidJson.getString("time_stamp"),
                                            bidJson.getString("bid_status")
                                    ));
                                }

                                // Log the updated bids list
                                Log.d("ApproveBidsActivity", "Updated bids list: " + bids.toString());

                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("ApproveBidsActivity", "Error in API response: " + response.getString("message"));
                            }
                        } catch (JSONException e) {
                            Log.e("ApproveBidsActivity", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ApproveBidsActivity", "Error fetching bids: " + error.getMessage());
                Toast.makeText(ApproveBidsActivity.this, "Error fetching bids: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);
    }

    public void approveBid(int bidId) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, APPROVE_BID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if (jsonResponse.getString("status").equals("success")) {
                                // Log the current bids list and compare the bidId
                                Log.d("ApproveBidsActivity", "Current bids list: " + bids);
                                boolean bidFound = false;

                                for (Bid bid : bids) {
                                    Log.d("ApproveBidsActivity", "Checking bid with ID: " + bid.getBid_id());
                                    if (bid.getBid_id() == bidId) {
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
                            } else {
                                Log.e("ApproveBidsActivity", "Approval failed: " + jsonResponse.getString("message"));
                            }
                        } catch (JSONException e) {
                            Log.e("ApproveBidsActivity", "Error processing response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ApproveBidsActivity", "Error approving bid: " + error.getMessage());
                        Toast.makeText(ApproveBidsActivity.this, "Error approving bid: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() {
                String requestBody = "{\"bid_id\":" + bidId + "}";
                Log.d("ApproveBidsActivity", "Request Body: " + requestBody);
                return requestBody.getBytes();
            }
        };

        queue.add(request);
    }

}

