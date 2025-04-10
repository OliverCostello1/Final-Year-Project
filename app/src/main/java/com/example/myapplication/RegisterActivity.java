package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText emailField, firstNameField, lastNameField, passwordField;
    private Spinner roleSpinner;
    private Button registerButton;
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
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> registerUser());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        roleSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString().trim();
        Log.d(TAG, "Selected role: " + role);

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String walletAddress;
        try {
            walletAddress = createEthereumAddress();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Wallet creation failed", e);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            CollectionReference usersRef = db.collection("users");
                            userDataToFirestore(usersRef, user.getUid(), email, firstName, lastName, password, role, walletAddress);
                        } else {
                            Toast.makeText(RegisterActivity.this, "User creation failed: User is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Authentication failed", task.getException());
                    }
                });
    }

    private void userDataToFirestore(CollectionReference usersRef, String userId, String email, String firstName, String lastName, String password, String role, String walletAddress) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userId);
        userData.put("walletAddress", walletAddress);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("role", role);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("userStatus", "pending");
        Log.d(TAG, "Saving user data: " + userData);

        DocumentReference userRef = usersRef.document(userId);
        userRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registration successful! Please wait for admin approval.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Firestore save failed", e);
                });
    }

    private String createEthereumAddress() {
        try {
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            }
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            return "0x" + Keys.getAddress(ecKeyPair);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to create Ethereum address: " + e.getMessage());
        }
    }
}
