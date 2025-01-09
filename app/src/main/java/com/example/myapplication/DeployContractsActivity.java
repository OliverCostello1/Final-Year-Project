package com.example.myapplication;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DeployContractsActivity extends AppCompatActivity {
    private TextView statusTextView;
    private static final String TAG = "DeployContractsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_contracts);

        statusTextView = findViewById(R.id.statusTextView);
        Button deployButton = findViewById(R.id.deploy_button);

        deployButton.setOnClickListener(v -> {
            statusTextView.setText(R.string.deploying_contracts);
            executeDeployScript();
        });
    }

    private void executeDeployScript() {
        new Thread(() -> {
            try {
                // Use AssetManager to access the file
                AssetManager assetManager = getAssets();
                InputStream inputStream = assetManager.open("contract/deploy.js");

                // Copy the script to a temporary file in internal storage
                File tempScript = new File(getFilesDir(), "deploy.js");
                FileOutputStream outputStream = new FileOutputStream(tempScript);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                // Use the temporary file path for the command
                String command = "node " + tempScript.getAbsolutePath();

                // Execute the command
                Process process = Runtime.getRuntime().exec(command);

                // Capture the output
                BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = stdOutput.readLine()) != null) {
                    output.append(line).append("\n");
                }

                StringBuilder errorOutput = new StringBuilder();
                while ((line = stdError.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }

                // Wait for the process to finish
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    updateStatus("Deployment successful:\n" + output);
                } else {
                    updateStatus("Deployment failed:\n" + errorOutput);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error executing deploy.js", e);
                updateStatus("Error: " + e.getMessage());
            }
        }).start();
    }


    private void updateStatus(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            statusTextView.setText(message);
            Toast.makeText(DeployContractsActivity.this, message, Toast.LENGTH_LONG).show();
        });
    }
}

