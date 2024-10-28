package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BidderActivity extends AppCompatActivity {
    private static final String TAG = "BidderActivity";
    private OkHttpClient client;
    private PropertyAdapter propertyAdapter;
    private RecyclerView propertyRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidder);

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

        // Fetch properties
        fetchProperties();
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
                runOnUiThread(() -> Toast.makeText(BidderActivity.this,
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
                            Toast.makeText(BidderActivity.this,
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
                    runOnUiThread(() -> Toast.makeText(BidderActivity.this,
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
                    Property property = new Property(
                            obj.getInt("property_id"),
                            obj.getString("eircode"),
                            obj.getString("link")
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
}
