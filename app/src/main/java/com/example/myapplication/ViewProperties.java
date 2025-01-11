package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewProperties extends AppCompatActivity {

    private PropertyAdapter2 propertyAdapter;
    private List<Property> propertyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_properties);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        propertyList = new ArrayList<>();
        propertyAdapter = new PropertyAdapter2(this, propertyList);
        recyclerView.setAdapter(propertyAdapter);

        // Fetch properties from the server
        fetchProperties();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }
    private void fetchProperties() {
        String url = "http://10.0.2.2:8000/project/view_properties.php"; // Replace with your actual URL

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                JSONArray data = response.getJSONArray("data");

                                // Clear the list to avoid duplication
                                propertyList.clear();

                                // Loop through the JSON array and add properties
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject propertyObject = data.getJSONObject(i);

                                    // Fetch data from the propertyObject
                                    String propertyId = propertyObject.getString("property_id");
                                    String eircode = propertyObject.getString("eircode");
                                    String link = propertyObject.getString("link");
                                    double askingPrice = propertyObject.getDouble("asking_price");
                                    int currentBid = propertyObject.getInt("current_bid");
                                    String auctioneerId = propertyObject.getString("auctioneer_id");
                                    String auctioneerWallet = propertyObject.getString("auctioneer_wallet");

                                    // Log each property field for debugging
                                    Log.i("PropertyData", "Property ID: " + propertyId);
                                    Log.i("PropertyData", "Eircode: " + eircode);
                                    Log.i("PropertyData", "Link: " + link);
                                    Log.i("PropertyData", "Asking Price: " + askingPrice);
                                    Log.i("PropertyData", "Auctioneer ID: " + auctioneerId);
                                    Log.i("PropertyData", "Auctioneer Wallet: " + auctioneerWallet);
                                    Log.i("PropertyData", "Current Bid: " + currentBid);

                                    // Add property to the list
                                    propertyList.add(new Property(propertyId, eircode, link, auctioneerId, askingPrice, currentBid, auctioneerWallet));
                                }

                                // Notify the adapter about data changes
                                propertyAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ViewProperties.this, "No properties found.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("ViewProperties", "JSON Parsing error: " + e.getMessage());
                            Toast.makeText(ViewProperties.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ViewProperties", "Volley error: " + error.getMessage());
                        Toast.makeText(ViewProperties.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

}
