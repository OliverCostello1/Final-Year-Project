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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserAdminActivity extends AppCompatActivity implements ApprovedUserAdapter.OnDeleteClickListener {
    private static final String TAG = "AdminActivity";
    private FirebaseFirestore db;
    private ApprovedUserAdapter userAdapter;
    private RecyclerView userRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recycler);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        userRecyclerView = findViewById(R.id.userRecycler);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list and delete click listener
        userAdapter = new ApprovedUserAdapter(new ArrayList<>(), this, this);
        userRecyclerView.setAdapter(userAdapter);

        // Fetch users from Firestore
        fetchUsers();

        // Lets user return to the previous page.

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void fetchUsers() {
        // Reference to the "users" collection in Firestore
        CollectionReference usersRef = db.collection("users");

        // Ensure admin user can't be deleted
        usersRef.whereNotEqualTo("role", "Admin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<User> users = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot) {
                            User user = document.toObject(User.class);
                            users.add(user);
                        }
                        if (users.isEmpty()) {
                            Toast.makeText(UserAdminActivity.this, "No users available", Toast.LENGTH_SHORT).show();
                            userRecyclerView.setVisibility(View.GONE);
                        } else {
                            userAdapter.updateData(users);
                            userRecyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e(TAG, "Error fetching users: ", task.getException());
                        Toast.makeText(UserAdminActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onDeleteClick(String id, int position) {
        deleteUserFromFirestore(id, position);
    }

    private void deleteUserFromFirestore(String userID, int position) {
        // Reference to the "users" collection
        CollectionReference usersRef = db.collection("users");

        // Delete the user document by ID
        usersRef.document(userID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User deleted successfully");
                    userAdapter.removeItem(position); // Remove item from adapter
                    Toast.makeText(UserAdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user: ", e);
                    Toast.makeText(UserAdminActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                });
    }
}
