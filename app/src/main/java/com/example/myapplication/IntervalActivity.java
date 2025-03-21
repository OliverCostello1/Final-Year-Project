package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.functions.FirebaseFunctionsException;

public class IntervalActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "IntervalActivity";



    // JavaScript interface class
    private class WebAppInterface {
        @JavascriptInterface
        public void updateSchedule(String frequency, String dayOfWeek) {
            Log.d(TAG, "JavaScript interface invoked. Frequency: " + frequency + ", Day: " + dayOfWeek);
            // Run on UI thread since this will be called from JavaScript
            runOnUiThread(() -> {
                IntervalActivity.this.updateSchedule(frequency, dayOfWeek);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);
        FirebaseApp.initializeApp(this);

        webView = findViewById(R.id.webView);
        setupWebView();
        loadWebPage();

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webView.clearCache(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Add JavaScript interface
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Add WebViewClient with error handling
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "WebView Error: " + description + " for URL: " + failingUrl);
                Toast.makeText(IntervalActivity.this,
                        "Error loading page: " + description, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "Page loaded successfully: " + url);
                Log.d("WebView", "Page finished loading: " + url);
            }
        });
    }

    private void loadWebPage() {
        try {
            String url = "https://fyp-bidder.web.app/interval_selection.html";
            webView.loadUrl(url);
        } catch (Exception e) {
            Log.e(TAG, "Error loading URL: " + e.getMessage());
            Toast.makeText(this, "Error loading page: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // method to call Firebase Function updateSchedule
    public void updateSchedule(String selectedFrequency, String selectedDayOfWeek) {
        if (selectedFrequency == null || selectedDayOfWeek == null) {
            Toast.makeText(this, "Please select both frequency and day",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Scheduling deploy script with frequency: " + selectedFrequency +
                " and day: " + selectedDayOfWeek);

        Map<String, String> data = new HashMap<>();
        data.put("frequency", selectedFrequency);
        data.put("day_of_week", selectedDayOfWeek);

        // Call Firebase Cloud Function updateSchedule
        FirebaseFunctions.getInstance("us-central1")
                .getHttpsCallable("updateSchedule")
                .call(data)
                .addOnSuccessListener(result -> {
                    Log.d(TAG, "Deploy script scheduled successfully");
                    Toast.makeText(IntervalActivity.this,
                            "Deploy Script Scheduled!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseFunctionsException && ((FirebaseFunctionsException) e).getCode() == FirebaseFunctionsException.Code.NOT_FOUND) {
                        // Handle "NOT_FOUND" error specifically
                        Log.e(TAG, "Cloud Function not found: " + e.getMessage());
                        Toast.makeText(IntervalActivity.this, "Cloud Function Not Found!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle other errors
                        Log.e(TAG, "Error scheduling deploy script:", e);
                    }

                    Log.e(TAG, "Error scheduling deploy script: " + e);
                    Log.e(TAG, "Error details: ", e); // Added error details
                    Toast.makeText(IntervalActivity.this,
                            "Error scheduling deploy script: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
