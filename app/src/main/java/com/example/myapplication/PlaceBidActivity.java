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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class PlaceBidActivity extends AppCompatActivity {
    private static final String TAG = "PlaceBidActivity";
    private OkHttpClient client;
    private PropertyAdapter propertyAdapter;
    private RecyclerView propertyRecyclerView;

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

        // Initialize OkHttpClient with longer timeout
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Fetch properties
        fetchProperties();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }

    private void fetchProperties() {
        String url = "http://10.0.2.2:8000/project/get_properties.php";

        // Build request with headers
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Log.d(TAG, "Fetching properties from: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network request failed", e);
                runOnUiThread(() -> Toast.makeText(PlaceBidActivity.this,
                        "Failed to fetch properties: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d(TAG, "Raw response: " + responseBody);

                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }

                    // Parse the response on a background thread
                    List<Property> properties = parseProperties(responseBody);

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        if (properties.isEmpty()) {
                            Log.d(TAG, "No properties found");
                            Toast.makeText(PlaceBidActivity.this,
                                    "No properties available",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Updating adapter with " + properties.size() + " properties");
                            propertyAdapter.updateData(properties);
                            propertyRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    runOnUiThread(() -> Toast.makeText(PlaceBidActivity.this,
                            "Error processing data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private List<Property> parseProperties(String jsonStr) {
        List<Property> properties = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonStr);
            Log.d(TAG, "Parsing JSON response: " + jsonStr);

            if (response.has("data")) {
                JSONArray data = response.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    // Sets the current bid to 0 if no bids made yet.
                    int current_bid = obj.optInt("current_bid", 0);
                    Property property = new Property(
                            obj.getString("property_id"),
                            obj.getString("eircode"),
                            obj.getString("link"),
                            obj.getString("auctioneer_id"),
                            obj.getInt("asking_price"),
                            current_bid,
                            obj.getString("auctioneer_wallet")
                    );
                    properties.add(property);

                    Log.d(TAG, "Parsed property: " + property.getPropertyId() +
                            ", " + property.getEircode());
                }
            } else {
                Log.e(TAG, "No 'data' field in response");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return properties;
    }

    public void submitBid(Property property, String userId, String userWallet, double bidAmount) {

        // This will be used to only allow bidder to submit a bid during business hours (Mon-Fri, 9am-5pm)
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        //
        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY && hourOfDay >=9   && hourOfDay <= 17) {

            String url = "http://10.0.2.2:8000/project/submit_bid.php";

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("property_id", property.getPropertyId());
                requestBody.put("bidder_id", userId);
                requestBody.put("bidder_wallet", userWallet);
                requestBody.put("auctioneer_id", property.getAuctioneer_id());
                requestBody.put("auctioneer_wallet", property.getAuctioneer_wallet());
                requestBody.put("bid_amount", bidAmount);
            } catch (JSONException e) {
                Log.e(TAG, "Error creating request body", e);
                return;
            }

            RequestBody formBody = RequestBody.create(
                    MediaType.parse("application/json"), requestBody.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();

            Log.d(TAG, "Submitting bid to: " + url);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Bid submission failed", e);
                    runOnUiThread(() -> Toast.makeText(PlaceBidActivity.this,
                            "Failed to submit bid: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        Log.d(TAG, "Bid submission response: " + responseBody);

                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected response code: " + response.code());
                        }

                        // Parse the response and update the UI as needed
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        runOnUiThread(() -> {
                            if (status.equals("success")) {
                                Toast.makeText(PlaceBidActivity.this, message, Toast.LENGTH_SHORT).show();
                                // Optionally, you can refresh the property list or update the current bid
                            } else {
                                Toast.makeText(PlaceBidActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Error processing bid response", e);
                        runOnUiThread(() -> Toast.makeText(PlaceBidActivity.this,
                                "Error processing bid response: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }else {
            runOnUiThread(() -> Toast.makeText(PlaceBidActivity.this,
                    "Bids can only be submitted between Monday and Friday, 9 AM to 5 PM.",
                    Toast.LENGTH_SHORT).show());
        }
    }
}