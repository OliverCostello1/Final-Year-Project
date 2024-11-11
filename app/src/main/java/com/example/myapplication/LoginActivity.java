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

import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    EditText emailField, passwordField;
    Button loginButton;

    String loginURL = "http://10.0.2.2:8000/project/login.php";
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    private void loginUser()  {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(loginURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        String status = json.getString("status");
                        String user_status = json.getString("user_status");
                        String role = json.getString("role");// getting the user's role to switch to the correct role
                        String userID = json.getString("id");
                        String first_name = json.getString("first_name");
                        String wallet_address = json.getString("wallet_address"); // To be used to place bid.
                        Log.d("SERVER RESPONSE: ", responseData);
                        Log.d("LoginActivity", "Role value: " + role + ", Type: " + ((Object) role).getClass().getSimpleName());
                        Log.d("LoginActivity", "Wallet Address:" + wallet_address);
                        if (status.equals("success")) {
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor  = prefs.edit();
                            editor.putString("user_id", userID);
                            editor.putString("user_status", user_status);
                            editor.putString("role", role);
                            editor.putString("first_name", first_name);
                            editor.putString("wallet_address", wallet_address);
                            editor.apply();

                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Welcome Back," + first_name + "!" , Toast.LENGTH_SHORT).show();
                                // Sends user to activity based on role type
                                Intent intent = switch (role) {
                                    case "admin" -> {
                                        Log.d("LoginActivity", role);

                                        yield new Intent(LoginActivity.this, AdminActivity.class);
                                    }
                                    case "auctioneer" -> {
                                        Log.d("LoginActivity", role);

                                        yield new Intent(LoginActivity.this, AuctioneerActivity.class);
                                    }
                                    case "bidder" -> {
                                        Log.d("LoginActivity", "Navigating to BidderActivity");

                                        yield new Intent(LoginActivity.this, BidderActivity.class);
                                    }
                                    default -> new Intent(LoginActivity.this, BidderActivity.class);
                                };
                                ;
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("LOGIN ERROR", "JSON PARSING ERROR" + e.getMessage());
                    }
                } else {
                    Log.e("LOGIN_ERROR", "Unsuccessful server response");
                }
            }


            // If Login fails   
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show());
                Log.e("LOGIN ERROR", "Network Failure" + e.getMessage());

            }
        });
    }
}
