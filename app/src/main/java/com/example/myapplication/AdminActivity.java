package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";
    private OkHttpClient client;
    private UserAdapter userAdapter;  // Assuming you have a UserAdapter for displaying users
    private RecyclerView userRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // Make sure you have a layout for AdminActivity

        // Initialize RecyclerView
        userRecyclerView = findViewById(R.id.userRecycler); // Make sure this ID matches your layout
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list
        userAdapter = new UserAdapter(new ArrayList<>(), this);
        userRecyclerView.setAdapter(userAdapter);

        // Initialize OkHttpClient with longer timeout
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Fetch users
        fetchUsers();
    }

    private void fetchUsers() {
        String url = "http://10.0.2.2:8000/project/get_users.php"; // Change this URL if necessary

        // Build request with headers
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Log.d(TAG, "Fetching users from: " + url);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network request failed", e);
                runOnUiThread(() -> Toast.makeText(AdminActivity.this,
                        "Failed to fetch users: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d(TAG, "Raw response: " + responseBody);

                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }

                    List<User> users = parseUsers(responseBody);

                    runOnUiThread(() -> {
                        if (users.isEmpty()) {
                            Log.d(TAG, "No users found");
                            Toast.makeText(AdminActivity.this, "No users available", Toast.LENGTH_SHORT).show();
                            userRecyclerView.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Updating adapter with " + users.size() + " users");
                            userAdapter.updateData(users);
                            userRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    runOnUiThread(() -> Toast.makeText(AdminActivity.this, "Error processing data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private List<User> parseUsers(String jsonStr) {
        List<User> users = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonStr);
            Log.d(TAG, "Parsing JSON response: " + jsonStr);

            if (response.has("data")) {
                JSONArray data = response.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    User user = new User(
                            obj.getString("wallet_address"),
                            obj.getString("first_name"),
                            obj.getString("last_name"),
                            obj.getString("role")
                    );
                    users.add(user);

                    Log.d(TAG, "Parsed user: " + user.getFirstName() + " " + user.getLastName());
                }
            } else {
                Log.e(TAG, "No 'data' field in response");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return users;
    }
}
