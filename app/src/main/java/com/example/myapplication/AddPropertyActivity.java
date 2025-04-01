package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

// Activity that allows Auctioneer user to add a property

public class AddPropertyActivity extends AppCompatActivity {

    private EditText etEircode, etLink, etAskingPrice;
    private Button btnSubmit;

    private String auctioneerId;
    private String auctioneerWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the XML layout file to be used for the activity.
        setContentView(R.layout.activity_add_property);
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);


        // Initialize the views
        etEircode = findViewById(R.id.eircode);
        etLink = findViewById(R.id.link);
        etAskingPrice = findViewById(R.id.asking_price);
        btnSubmit = findViewById(R.id.submit_button);

        // Retrieve auctioneer ID and wallet from SharedPreferences ( user_prefs )
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        auctioneerId = prefs.getString("user_id", "");  // Default value of -1 if not found
        auctioneerWallet = prefs.getString("wallet_address", "");  // Default empty string if not found

        // Log the auctioneer details for debugging
        Log.d("AddPropertyActivity", "Auctioneer ID: " + auctioneerId);
        Log.d("AddPropertyActivity", "Auctioneer Wallet: " + auctioneerWallet);

        // Set the button click listener to submit the property details
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddPropertyActivity", "Submit button clicked");

                String eircode = etEircode.getText().toString();
                String link = etLink.getText().toString();
                String askingPrice = etAskingPrice.getText().toString();

                if (eircode.isEmpty() || link.isEmpty() || askingPrice.isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double price = Double.parseDouble(askingPrice);
                        Log.d("AddPropertyActivity", "Eircode: " + eircode + ", Link: " + link + ", Asking Price: " + price);
                        addPropertyToFirestore(eircode, link, price);
                    } catch (NumberFormatException ex) {
                        Log.e("AddPropertyActivity", "Invalid asking price: " + askingPrice);
                        Toast.makeText(AddPropertyActivity.this, "Invalid asking price", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Lets user return to the previous page
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Method to send property data to Firebase Realtime Database
    private void addPropertyToFirestore(String eircode, String link, double askingPrice) {
        // Get a reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference propertiesRef = db.collection("properties");

        // Create a unique ID for the property using Firestore's auto-ID feature
        String propertyId = propertiesRef.document().getId();

        // Create a Property object
        Property property = new Property(
                propertyId, // Use propertyId as a String
                eircode,
                link,
                auctioneerId,
                askingPrice,
                0, // Current bid is 0 initially
                auctioneerWallet
        );

        // Save the property to Firestore
        propertiesRef.document(propertyId).set(property)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AddPropertyActivity", "Property added to Firestore");
                    Toast.makeText(AddPropertyActivity.this, "Property added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddPropertyActivity", "Error adding property: " + e.getMessage());
                    Toast.makeText(AddPropertyActivity.this, "Error adding property", Toast.LENGTH_SHORT).show();
                });
    }
}
