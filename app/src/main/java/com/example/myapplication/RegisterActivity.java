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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.crypto.exception.CipherException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import okhttp3.*;

// ACTIVITY TO REGISTER NEW USER

public class RegisterActivity extends AppCompatActivity {

    EditText emailField, firstNameField, lastNameField, passwordField;
    Spinner roleSpinner;
    Button registerButton;
    String registerURL = "http://10.0.2.2/project/register.php";

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
    }



    private void registerUser() {
        String email = emailField.getText().toString();
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String password = passwordField.getText().toString();
        String role = roleSpinner.getSelectedItem().toString();

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

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("first_name", firstName)
                .add("last_name", lastName)
                .add("password", password)
                .add("role", role)
                .add("wallet_address", walletAddress)
                .build();

        Request request = new Request.Builder()
                .url(registerURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this,
                            "Network error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this,
                                "Registration successful!",
                                Toast.LENGTH_SHORT).show();

                        Log.d("RegisterActivity", "Selected Role: " + role);
                    });

                    Intent intent;
                    switch (role) {
                        case "Admin":
                            Log.d("RegisterActivity", role);

                            intent = new Intent(RegisterActivity.this, AdminActivity.class);
                            break;
                        case "Auctioneer":
                            Log.d("RegisterActivity", role);

                            intent = new Intent(RegisterActivity.this, AuctioneerActivity.class);
                            break;
                        case "Bidder":
                            Log.d("RegisterActivity", "Navigating to BidderActivity");

                            intent = new Intent(RegisterActivity.this, BidderActivity.class);
                            break;

                        default:
                            intent = new Intent(RegisterActivity.this, BidderActivity.class);
                            break;

                    }
                    intent.putExtra("wallet_address", walletAddress);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = response.body() != null ?
                            response.body().string() :
                            "Unknown error occurred";
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private String createEthereumAddress() {
        try {
            // Initialize BouncyCastle
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            // Generate key pair
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();

            // Get the Ethereum address directly from the key pair
            return Keys.getAddress(ecKeyPair);

        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create Ethereum address: " + e.getMessage());
        }
    }}


