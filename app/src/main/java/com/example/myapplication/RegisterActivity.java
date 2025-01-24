package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import jnr.a64asm.Register;

public class RegisterActivity extends AppCompatActivity {

    EditText emailField, firstNameField, lastNameField, passwordField;
    Spinner roleSpinner;
    Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.email);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        passwordField = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.role);
        registerButton = findViewById(R.id.register);

        Button returnHome = findViewById(R.id.register_return);
        returnHome.setOnClickListener(view -> {
            Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(registerIntent);
        });

        registerButton.setOnClickListener(v -> registerUser());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void registerUser() {
        String email = emailField.getText().toString();
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String password = passwordField.getText().toString();
        String role = roleSpinner.getSelectedItem().toString();
        Log.d("RegisterActivity", "Selected role: " + role);

        // Input validation
        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Ethereum address
        String walletAddress;
        try {
            walletAddress = createEthereumAddress();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is successfully created in Firebase Authentication
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Create Firestore user data
                            CollectionReference usersRef = db.collection("users");

                            // Prepare user data for Firestore
                            userDataToFirestore(usersRef, user.getUid(), email, firstName, lastName, password, role, walletAddress);
                        }
                    } else {
                        // If sign up fails, display a message to the user
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(String.valueOf(RegisterActivity.this), task.getException().getMessage());
                    }
                });
    }

    private void userDataToFirestore(CollectionReference usersRef, String userId, String email, String firstName, String lastName, String password, String role, String walletAddress) {
        // Prepare user data using a Map
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userId);
        userData.put("walletAddress", walletAddress);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("role", role);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("userStatus", "pending");
        Log.d("RegisterActivity", "User role: " + role);

        Log.d("RegisterActivity", "Saving user data: " + userData);

        // Add user data to Firestore
        DocumentReference userRef = usersRef.document(userId);
        userRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate based on the role
                    Intent intent;
                    switch (role) {
                        case "Auctioneer":
                            intent = new Intent(RegisterActivity.this, AuctioneerActivity.class);
                            break;
                        case "Bidder":
                            intent = new Intent(RegisterActivity.this, BidderActivity.class);
                            break;
                        case "Admin":
                            intent = new Intent(RegisterActivity.this, AdminActivity.class);
                            break;
                        default:
                            Log.d("RegisterActivity", "Unexecpted Role:" + role);
                            intent = new Intent(RegisterActivity.this, MainActivity.class);
                            break;

                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(String.valueOf(RegisterActivity.this), e.getMessage());
                });
    }


    private String createEthereumAddress() {
        try {
            // Initialize BouncyCastle
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            }

            // Generate key pair
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            return "0x" + Keys.getAddress(ecKeyPair);

        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                 NoSuchProviderException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create Ethereum address: " + e.getMessage());
        }
    }

    // Users class to store user data
    static class Users {
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private String role;
        private String walletAddress;
        private String userStatus;

        // Required empty constructor for Firebase
        public Users() {
        }

        public Users(String email, String firstName, String lastName, String password, String role, String walletAddress) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.password = password;
            this.role = role;
            this.walletAddress = walletAddress;
            this.userStatus = "pending";
        }

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getWalletAddress() {
            return walletAddress;
        }

        public void setWalletAddress(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }
    }
}