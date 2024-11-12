package com.example.myapplication;

import static android.content.ContentValues.TAG;

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

public class PendingUsersActivity extends AppCompatActivity implements PendingUserAdapter.OnApproveClickListener {
    private PendingUserAdapter userAdapter;
    private RecyclerView userRecyclerView;
    private OkHttpClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recycler);

        // Initialize RecyclerView
        userRecyclerView = findViewById(R.id.userRecycler);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list and approve click listener
        userAdapter = new PendingUserAdapter(new ArrayList<>(), this, this);
        userRecyclerView.setAdapter(userAdapter);

        // Initialize OkHttpClient with timeout
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        fetchUsers();
    }

    private void fetchUsers() {
        String url = "http://10.0.2.2:8000/project/get_pending_users.php";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(PendingUsersActivity.this,
                        "Failed to fetch users: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                List<User> users = parseUsers(responseBody);
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
        });
    }

    @Override
    public void onApproveClick(int userId, int position) {
        approveUser(userId, position);
    }

    private void approveUser(int userId, int position) {
        String url = "http://10.0.2.2:8000/project/approve_users.php";
        RequestBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(userId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(PendingUsersActivity.this,
                        "Failed to approve user: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(PendingUsersActivity.this, "User approved successfully", Toast.LENGTH_SHORT).show();
                        userAdapter.removeItem(position);  // Remove user from list after approval
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(PendingUsersActivity.this,
                            "Failed to approve user: " + response.message(),
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private List<User> parseUsers(String jsonStr) {
        List<User> users = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(jsonStr);
            if (response.has("data")) {
                JSONArray data = response.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    User user = new User(
                            obj.getInt("id"),
                            obj.getString("wallet_address"),
                            obj.getString("first_name"),
                            obj.getString("last_name"),
                            obj.getString("role")
                    );
                    user.setId(obj.getInt("id"));
                    users.add(user);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return users;
    }
}
