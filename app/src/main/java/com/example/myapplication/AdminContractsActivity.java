package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminContractsActivity extends AppCompatActivity {

    private RecyclerView contractsRecyclerView;
    private ContractAdapter adapter;
    private List<Contract> contractList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_contracts);

        contractsRecyclerView = findViewById(R.id.contractsRecyclerView);
        contractsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContractAdapter(this, contractList);
        contractsRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchContracts();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }
    // Ordered by time, showing newest contract first.
    private void fetchContracts() {
        db.collection("contracts")
                .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING) // Order by created_at, latest first
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contractList.clear(); // Clear the list to avoid duplicate entries on reload
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String bidId = snapshot.getString("bid_id");
                        String contractAddress = snapshot.getString("contract_address");
                        String transactionHash = snapshot.getString("transaction_hash");
                        String createdAt = snapshot.getTimestamp("created_at").toDate().toString();
                        contractList.add(new Contract(bidId, contractAddress, transactionHash, createdAt));
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter about the data change
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching contracts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}