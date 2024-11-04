package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
                        String role = json.getString("role"); // getting the user's role to switch to the correct role
                        Log.d("SERVER RESPONSE: ", responseData);

                        if (status.equals("success")) {

                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Welcome Back!" , Toast.LENGTH_SHORT).show();
                                // Sends user to activity based on role type
                                Intent intent = switch (role) {
                                    case "admin" ->
                                            new Intent(LoginActivity.this, AdminActivity.class);
                                    case "auctioneer" ->
                                            new Intent(LoginActivity.this, AuctioneerActivity.class);
                                    default -> new Intent(LoginActivity.this, BidderActivity.class);
                                };
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("LOGIN ERROR", "JSON PARSNING ERROR" + e.getMessage());
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
