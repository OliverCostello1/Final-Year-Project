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
    private TextView userTextView;
    private TextView propertyTextView;
    private TextView bidTextView;
    private TextView highest_bidder_id;
    private TextView highest_bid_amount;
    private TextView most_bid_property_id;
    private TextView most_bid_property_count;
    private TextView auctioneer_most_property_id;
    private TextView auctioneer_most_property_count;
    private TextView approval_count;
    private TextView property_id_asking_priceTextView;
    private TextView property_asking_priceTextView;
    private TextView bidder_id_most_bidsTextView;
    private TextView bidder_count_most_bidsTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);
        userTextView = findViewById(R.id.user_count);
        propertyTextView = findViewById(R.id.property_count);
        bidTextView = findViewById(R.id.bid_count);
        highest_bidder_id = findViewById(R.id.highest_bidder_id);
        highest_bid_amount = findViewById(R.id.highest_bid_amount);
        most_bid_property_id = findViewById(R.id.most_bid_property_id);
        most_bid_property_count = findViewById(R.id.most_bid_property_count);
        auctioneer_most_property_id = findViewById(R.id.auctioneer_most_property_id);
        auctioneer_most_property_count = findViewById(R.id.auctioneer_most_property_count);
        approval_count = findViewById(R.id.users_waiting_approval);
        property_id_asking_priceTextView = findViewById(R.id.property_id_asking_price);
        property_asking_priceTextView = findViewById(R.id.property_asking_price);
        bidder_id_most_bidsTextView = findViewById(R.id.bidder_id_most_bids);
        bidder_count_most_bidsTextView = findViewById(R.id.bidder_count_most_bids);

        String url = "http://10.0.2.2:8000/project/usage_count.php";
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
                            JSONObject data = jsonResponse.optJSONObject("data");
                            if (data != null) {
                                int propertiesCount = data.optInt("properties_count", 0);
                                int usersCount = data.optInt("users_count", 0);
                                int bidsCount = data.optInt("bids_count", 0);
                                int users_waiting_approval = data.optInt("pending_count", 0);

                                // Access the nested "highest_bid" object
                                JSONObject highestBid = data.optJSONObject("highest_bid");
                                int highestBidderId = 0; // Default value
                                int highest_bid = 0;
                                if (highestBid != null) {
                                    highestBidderId = highestBid.optInt("bidder_id", 0); // Default to 0 if not found
                                    highest_bid = highestBid.optInt("bid_amount",  0);
                                }

                                JSONObject most_bids_property = data.optJSONObject("most_bids_property");
                                int propertyID = 0;
                                int bid_count = 0;
                                if (most_bids_property != null ) {
                                    propertyID = most_bids_property.optInt("property_id", 0);
                                    bid_count = most_bids_property.optInt("bid_count", 0);
                                }

                                JSONObject auctioneer_most_properties = data.optJSONObject("auctioneer_most_properties");
                                int auctioneer_id = 0;
                                int property_count = 0;
                                if (auctioneer_most_properties != null) {
                                    auctioneer_id = auctioneer_most_properties.optInt("auctioneer_id", 0);
                                    property_count = auctioneer_most_properties.optInt("property_count", 0);
                                }


                                JSONObject property_asking_price = data.optJSONObject("property_asking_price");
                                int property_id2 = 0;
                                int asking_price = 0;
                                if (property_asking_price != null) {
                                    property_id2 = property_asking_price.optInt("property_id", 0);
                                    asking_price = property_asking_price.optInt("asking_price", 0);
                                }

                                JSONObject bidder_most_bids = data.optJSONObject("bidder_most_bids");
                                int bidder_id = 0;
                                int bidder_count = 0;

                                if (bidder_most_bids != null ) {
                                    bidder_id =bidder_most_bids.optInt("bidder_id", 0);
                                    bidder_count = bidder_most_bids.optInt("bid_count", 0 );
                                }

                                // Display the row counts in the UI
                                propertyTextView.setText(getString(R.string.property_count_label, propertiesCount));
                                userTextView.setText(getString(R.string.user_count_label, usersCount));
                                bidTextView.setText(getString(R.string.bid_count_label, bidsCount));
                                approval_count.setText(getString(R.string.number_of_users_waiting_approval, users_waiting_approval));
                                highest_bidder_id.setText(getString(R.string.bidder_id_of_highest_bid, highestBidderId));
                                highest_bid_amount.setText(getString(R.string.highest_bid_amount, highest_bid));
                                most_bid_property_id.setText(getString(R.string.property_id_with_most_bids, propertyID));
                                most_bid_property_count.setText(getString(R.string.bid_count_of_most_popular_property_1_d, bid_count));
                                auctioneer_most_property_id.setText(getString(R.string.auctioneer_id_with_most_properties_1_d, auctioneer_id));
                                auctioneer_most_property_count.setText(getString(R.string.property_count_for_auctioneer_with_most_properties, property_count));
                                property_id_asking_priceTextView.setText(getString(R.string.property_id_with_highest_asking_price, property_id2));
                                property_asking_priceTextView.setText(getString(R.string.highest_asking_price_for_a_property, asking_price));
                                bidder_id_most_bidsTextView.setText(getString(R.string.bidder_id_with_most_bids, bidder_id));
                                bidder_count_most_bidsTextView.setText(getString(R.string.most_number_of_bids_submitted_by_a_user, bidder_count));
                            }
                        } else {
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