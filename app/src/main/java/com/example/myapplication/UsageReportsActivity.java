package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// SHOWS USAGE REPORTS OF APP FOR ADMIN USER.
public class UsageReportsActivity extends AppCompatActivity {
    private TextView userTextView, propertyTextView, bidTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);
        userTextView = findViewById(R.id.user_count);
        propertyTextView = findViewById(R.id.property_count);
        bidTextView = findViewById(R.id.bid_count);

        String url = "http://10.0.2.2/project/usage_count.php";
        fetchRowCounts(url);

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }
    private void fetchRowCounts(String urlString) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder response = new StringBuilder();

            try {
                // Create URL object and open a connection
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the response
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Process the JSON response on the main thread
                mainHandler.post(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.optString("status").equals("success")) {
                            JSONObject data = jsonResponse.getJSONObject("data");
                            int propertiesCount = data.optInt("properties", 0);
                            int usersCount = data.optInt("users", 0);
                            int bidsCount = data.optInt("bids", 0);

                            // Display the row counts in the UI
                            propertyTextView.setText(getString(R.string.property_count_label, propertiesCount));
                            userTextView.setText(getString(R.string.user_count_label, usersCount));
                            bidTextView.setText(getString(R.string.bid_count_label, bidsCount));
                        }

                        else {

                            Log.e("RowCountFetcher", "Status not success in JSON response");

                        }
                    } catch (Exception e) {
                        Log.e("RowCountFetcher", "Error parsing JSON", e);
                    }
                });

            } catch (Exception e) {
                Log.e("RowCountFetcher", "Error fetching row counts", e);
                mainHandler.post(() -> {
                    // Handle error scenario
                    propertyTextView.setText(getString(R.string.error_fetching_data));
                    userTextView.setText(getString(R.string.error_fetching_data));
                    bidTextView.setText(getString(R.string.error_fetching_data));
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    Log.e("RowCountFetcher", "Error closing reader", e);
                }
            }
        });
    }

}
