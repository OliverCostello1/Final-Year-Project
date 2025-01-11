package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.datatypes.Int;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Button to let user return home
        Button returnHome = findViewById(R.id.go_back);
        returnHome.setOnClickListener(view -> {
            Intent registerIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(registerIntent);
        });

        emailField = findViewById(R.id.email_login);
        passwordField = findViewById(R.id.password_login);
        loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Fetch additional user data from Firestore
                            fetchUserDataAndNavigate(user.getUid());
                        }
                    } else {
                        // Handle login failure
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Login failed";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("LOGIN_ERROR", "Authentication failed", task.getException());
                    }
                });
    }

    private void fetchUserDataAndNavigate(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Store user data in SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString("user_id", userId);
                        editor.putString("user_status", documentSnapshot.getString("userStatus"));
                        editor.putString("role", documentSnapshot.getString("role"));
                        editor.putString("first_name", documentSnapshot.getString("firstName"));
                        editor.putString("wallet_address", documentSnapshot.getString("walletAddress"));
                        editor.apply();

                        String firstName = documentSnapshot.getString("firstName");
                        String role = documentSnapshot.getString("role");

                        Toast.makeText(LoginActivity.this,
                                "Welcome Back, " + firstName + "!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate based on role
                        Intent intent = switch (role != null ? role : "") {
                            case "Admin" -> {
                                Log.d("LoginActivity", "Navigating to AdminActivity");
                                yield new Intent(LoginActivity.this, AdminActivity.class);
                            }
                            case "Auctioneer" -> {
                                Log.d("LoginActivity", "Navigating to AuctioneerActivity");
                                yield new Intent(LoginActivity.this, AuctioneerActivity.class);
                            }
                            case "Bidder" -> {
                                Log.d("LoginActivity", "Navigating to BidderActivity");
                                yield new Intent(LoginActivity.this, BidderActivity.class);
                            }
                            default -> {
                                Log.d("LoginActivity", "Role not recognized, defaulting to BidderActivity");
                                yield new Intent(LoginActivity.this, BidderActivity.class);
                            }
                        };

                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("LOGIN_ERROR", "User document not found in Firestore");
                        Toast.makeText(LoginActivity.this,
                                "Error: User data not found",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LOGIN_ERROR", "Failed to fetch user data", e);
                    Toast.makeText(LoginActivity.this,
                            "Error fetching user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}