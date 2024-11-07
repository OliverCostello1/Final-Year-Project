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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserAdminActivity extends AppCompatActivity implements UserAdapter.OnDeleteClickListener {
    private static final String TAG = "AdminActivity";
    private OkHttpClient client;
    private UserAdapter userAdapter;
    private RecyclerView userRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin);

        // Initialize RecyclerView
        userRecyclerView = findViewById(R.id.userRecycler);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list and delete click listener
        userAdapter = new UserAdapter(new ArrayList<>(), this, this);
        userRecyclerView.setAdapter(userAdapter);

        // Initialize OkHttpClient with timeout
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        Log.d(TAG, "onCreate: Fetching users from database");

        // Fetch users from the database
        fetchUsers();
    }

    private void fetchUsers() {
        String url = "http://10.0.2.2:8000/project/get_users.php"; // Update URL as needed
        Log.d(TAG, "fetchUsers: Sending request to " + url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(UserAdminActivity.this,
                        "Failed to fetch users: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }

                    List<User> users = parseUsers(responseBody);
                    runOnUiThread(() -> {
                        if (users.isEmpty()) {
                            Toast.makeText(UserAdminActivity.this, "No users available", Toast.LENGTH_SHORT).show();
                            userRecyclerView.setVisibility(View.GONE);
                        } else {
                            userAdapter.updateData(users);
                            userRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(UserAdminActivity.this, "Error processing data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private List<User> parseUsers(String jsonStr) {
        List<User> users = new ArrayList<>();
        try {

            Log.d(TAG, "parseUsers: Parsing JSON Data " + users.size());
            JSONObject response = new JSONObject(jsonStr);
            if (response.has("data")) {
                JSONArray data = response.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    User user = new User(
                            obj.getString("id"),
                            obj.getString("wallet_address"),
                            obj.getString("first_name"),
                            obj.getString("last_name"),
                            obj.getString("role")
                    );
                    if (obj.has("id")) {
                        user.setId(obj.getInt("id"));  // Ensure your User class has setId method
                    } else {
                        Log.e(TAG, "No id found for user at position " + i);
                    }
                    users.add(user);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        Log.d(TAG, "onResponse: Parsed Users " + users.size());

        return users;
    }
    @Override
    public void onDeleteClick(String id, int position) {
        deleteUserFromDatabase(id, position);
        Log.d(TAG, "Called deleteUserFromDatabase method");
    }
    private void deleteUserFromDatabase(String userID, int position) {
        String url = "http://10.0.2.2:8000/project/delete_user.php";
        Log.d(TAG, "Attempting to delete user with user id: " + userID);

        RequestBody formBody = new FormBody.Builder()
                .add("id", userID)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to delete user: ", e);
                runOnUiThread(() -> Toast.makeText(UserAdminActivity.this,
                        "Failed to delete user: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "Delete response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "User deleted successfully");
                    runOnUiThread(() -> {
                        userAdapter.removeItem(position); // Remove item from adapter
                        Toast.makeText(UserAdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e(TAG, "Failed to delete user from database");
                    runOnUiThread(() -> Toast.makeText(UserAdminActivity.this,
                            "Failed to delete user from database",
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}