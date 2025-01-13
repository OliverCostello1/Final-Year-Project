package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class PendingUsersActivity extends AppCompatActivity implements PendingUserAdapter.OnApproveClickListener {
    private PendingUserAdapter userAdapter;
    private RecyclerView userRecyclerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recycler);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        userRecyclerView = findViewById(R.id.userRecycler);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list and approve click listener
        userAdapter = new PendingUserAdapter(new ArrayList<>(), this, this);
        userRecyclerView.setAdapter(userAdapter);

        // Fetch users from Firestore
        fetchUsers();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void fetchUsers() {
        // Firestore reference to the pending users collection
        CollectionReference usersRef = db.collection("users");

        // Query for pending users
        Query query = usersRef.whereEqualTo("userStatus", "pending");

        // Fetch users from Firestore
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<User> users = new ArrayList<>();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Loop through the documents and add them to the list
                    for (var document : querySnapshot.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            users.add(user);
                        }
                    }

                    // Update UI
                    runOnUiThread(() -> {
                        if (users.isEmpty()) {
                            Toast.makeText(PendingUsersActivity.this, "No users available", Toast.LENGTH_SHORT).show();
                            userRecyclerView.setVisibility(View.GONE);
                        } else {
                            userAdapter.updateData(users);
                            userRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(PendingUsersActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onApproveClick(String userId, int position) {
        approveUser(userId, position);  // Now accepting String userId
    }

    private void approveUser(String userId, int position) {
        // Firestore reference to the pending users collection
        CollectionReference usersRef = db.collection("users");
        usersRef.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("PendingUsersActivity", "User document found, proceeding with approval.");
                usersRef.document(userId).update("userStatus", "approved")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(PendingUsersActivity.this, "User approved successfully", Toast.LENGTH_SHORT).show();
                            userAdapter.removeItem(position);  // Remove user from list after approval
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PendingUsersActivity.this, "Failed to approve user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.d("PendingUsersActivity", "User document not found.");
                Toast.makeText(PendingUsersActivity.this, "User document not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch the user document by userId
        Log.d("PendingUsersActivity", "Approving user with ID: " + userId);

        usersRef.document(userId).update("userStatus", "approved")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PendingUsersActivity.this, "User approved successfully", Toast.LENGTH_SHORT).show();
                    userAdapter.removeItem(position);  // Remove user from list after approval
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PendingUsersActivity.this, "Failed to approve user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
