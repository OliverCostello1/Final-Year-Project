package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {

    private EditText etEircode, etLink, etAskingPrice, etCurrentBid;
    private Button btnSubmit;

    private int auctioneerId;
    private String auctioneerWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        // Initialize the views
        etEircode = findViewById(R.id.eircode);
        etLink = findViewById(R.id.link);
        etAskingPrice = findViewById(R.id.asking_price);
        btnSubmit = findViewById(R.id.submit_button);

        // Retrieve auctioneer ID and wallet from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        auctioneerId = prefs.getInt("user_id", -1);  // Default value of -1 if not found
        auctioneerWallet = prefs.getString("wallet_address", "");  // Default empty string if not found

        // Log the auctioneer details for debugging
        Log.d("AddPropertyActivity", "Auctioneer ID: " + auctioneerId);
        Log.d("AddPropertyActivity", "Auctioneer Wallet: " + auctioneerWallet);

        // Set the button click listener to submit the property details
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the input fields
                String eircode = etEircode.getText().toString();
                String link = etLink.getText().toString();
                String askingPrice = etAskingPrice.getText().toString();

                if (eircode.isEmpty() || link.isEmpty() || askingPrice.isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Send the property data to the server
                    sendPropertyToServer(eircode, link, askingPrice);
                }
            }
        });
    }

    // Method to send property data to the PHP endpoint
    private void sendPropertyToServer(final String eircode, final String link, final String askingPrice) {
        // URL of your PHP endpoint
        String url = "http://10.0.2.2:8000/project/add_property.php";

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Prepare the parameters to be sent in the POST request
        Map<String, String> params = new HashMap<>();
        params.put("eircode", eircode);
        params.put("link", link);
        params.put("asking_price", askingPrice);
        params.put("auctioneer_id", String.valueOf(auctioneerId));  // Auctioneer ID from SharedPreferences
        params.put("auctioneer_wallet", auctioneerWallet);          // Auctioneer Wallet from SharedPreferences
        params.put("current_bid", String.valueOf(0));                       // Current bid (can be 0 or empty)

        // Create a new StringRequest for the POST request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from the server
                        Log.d("AddPropertyActivity", "Response: " + response);
                        Toast.makeText(AddPropertyActivity.this, "Property added successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AddPropertyActivity", "Error: " + error.toString());
                        Toast.makeText(AddPropertyActivity.this, "Error adding property", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Return the parameters to be sent in the POST request
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(stringRequest);
    }
}
