package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewPropertiesActivity extends AppCompatActivity {

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

        // Fetch properties from Firestore
        fetchPropertiesFromFirestore();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void fetchPropertiesFromFirestore() {
        // Firebase Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the properties collection
        CollectionReference propertiesRef = db.collection("properties");

        // Query Firestore for all properties
        Query query = propertiesRef;

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Clear the previous data
                    propertyList.clear();

                    // Loop through the documents and add them to the list
                    querySnapshot.forEach(document -> {
                        // Fetch data from the document
                        String propertyId = document.getString("propertyId");
                        String eircode = document.getString("eircode");
                        String link = document.getString("link");
                        Double askingPrice = document.getDouble("asking_price");
                        Long currentBidLong = document.getLong("current_bid");
                        int currentBid = currentBidLong != null ? currentBidLong.intValue() : 0;
                        String auctioneerId = document.getString("auctioneer_id");
                        String auctioneerWallet = document.getString("auctioneer_wallet");

                        // Log each property field for debugging
                        Log.i("PropertyData", "Property ID: " + propertyId);
                        Log.i("PropertyData", "Eircode: " + eircode);
                        Log.i("PropertyData", "Link: " + link);
                        Log.i("PropertyData", "Asking Price: " + askingPrice);
                        Log.i("PropertyData", "Auctioneer ID: " + auctioneerId);
                        Log.i("PropertyData", "Auctioneer Wallet: " + auctioneerWallet);
                        Log.i("PropertyData", "Current Bid: " + currentBid);

                        // Add the property to the list
                        propertyList.add(new Property(propertyId, eircode, link, auctioneerId, askingPrice, currentBid, auctioneerWallet));
                    });

                    // Notify the adapter about data changes
                    propertyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ViewPropertiesActivity.this, "No properties found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ViewProperties", "Error getting documents: ", task.getException());
                Toast.makeText(ViewPropertiesActivity.this, "Failed to fetch properties.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
