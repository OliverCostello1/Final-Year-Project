package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class AdminActivity extends AppCompatActivity  {
    private static final String TAG = "AdminActivity";
    public Button user_management, usage_reports, view_properties, approve_users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        user_management =findViewById(R.id.user_management);
        usage_reports = findViewById(R.id.usage_report);
        view_properties = findViewById(R.id.all_properties);
        approve_users = findViewById(R.id.approve_users);
        user_management.setOnClickListener(view-> {
            Intent intent = new Intent(AdminActivity.this, UserAdminActivity.class);
            startActivity(intent);
        });
        usage_reports.setOnClickListener(view-> {
            Intent intent = new Intent(AdminActivity.this, UsageReportsActivity.class);
            startActivity(intent);
        });


        view_properties.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ViewProperties.class);
            startActivity(intent);
        });


        approve_users.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, PendingUsersActivity.class );
            startActivity(intent);
        });

        };
        // Initialize RecyclerView


}

