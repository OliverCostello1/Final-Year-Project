package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

public class DeployContractsActivity extends AppCompatActivity {

    private TextView statusTextView;
    private WebView webView;
    private Button deployButton;
    private boolean isDeploying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_contracts);

        statusTextView = findViewById(R.id.statusTextView);
        deployButton = findViewById(R.id.deploy_button);
        webView = findViewById(R.id.webview);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        deployButton.setOnClickListener(v -> {
            if (!isDeploying) {
                isDeploying = true;
                deployButton.setEnabled(false);
                statusTextView.setText(R.string.deploying_contracts);
                executeDeployScriptInWebView();
            }
        });
    }

    private void executeDeployScriptInWebView() {
        // Load the hosted JavaScript file into the WebView
        webView.loadUrl("https://fyp-bidder.web.app/index.html");

        // Optionally handle WebView load events or errors
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                updateStatus("Deployment script loaded.");
            }

            @Override
            public void onReceivedError(android.webkit.WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) {
                super.onReceivedError(view, request, error);
                updateStatus("Error loading deployment script.");
            }
        });
    }

    private void updateStatus(String message) {
        if (!isFinishing() && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            new Handler(Looper.getMainLooper()).post(() -> {
                statusTextView.setText(message);
                Toast.makeText(DeployContractsActivity.this, message, Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Reset WebView to prevent memory leaks
        if (webView != null) {
            webView.destroy();
        }
    }
}
