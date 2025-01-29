package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.web3j.protocol.admin.Admin;

// Admin user home page
public class AdminActivity extends AppCompatActivity  {
    private static final String TAG = "AdminActivity";
    public Button user_management, usage_reports, view_properties, view_contracts, approve_users, logout, deploy_contracts, deployment_interval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        user_management =findViewById(R.id.user_management);
        usage_reports = findViewById(R.id.usage_report);
        view_properties = findViewById(R.id.all_properties);
        approve_users = findViewById(R.id.approve_users);
        logout = findViewById(R.id.logout_button);
        view_contracts = findViewById(R.id.all_contracts);
        deploy_contracts = findViewById(R.id.deploy_contract);
        deployment_interval = findViewById(R.id.contract_deployment_interval);


        // Navigation buttons for admin user
        user_management.setOnClickListener(view-> {
            Intent intent = new Intent(AdminActivity.this, UserAdminActivity.class);
            startActivity(intent);
        });
        usage_reports.setOnClickListener(view-> {
            Intent intent = new Intent(AdminActivity.this, UsageReportsActivity.class);
            startActivity(intent);
        });


        view_properties.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ViewPropertiesActivity.class);
            startActivity(intent);
        });

        view_contracts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminContractsActivity.class);
            startActivity(intent);
        });

        approve_users.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, PendingUsersActivity.class );
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
        });
        deploy_contracts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, DeployContractsActivity.class);
            startActivity(intent);
        });

        deployment_interval.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, IntervalActivity.class);
            startActivity(intent);
        });

        };


}

