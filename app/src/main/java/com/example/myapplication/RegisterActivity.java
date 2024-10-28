package com.example.myapplication;

import static java.sql.Types.NULL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.security.Security;

import okhttp3.*;
// Used to generate Wallet id for users
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
public class RegisterActivity extends AppCompatActivity{

    EditText emailField, firstNameField, lastNameField, passwordField, houseAddressField;
    Spinner roleSpinner;
    Button registerButton;
    String registerURL = "http://10.0.2.2:8000/project/register.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.email);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        passwordField = findViewById(R.id.password);
        houseAddressField = findViewById(R.id.house_address);
        roleSpinner = findViewById(R.id.role);
        registerButton = findViewById(R.id.register);

        Button returnHome = findViewById(R.id.register_return);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Adding items to role Spinner: Bidder, Auctioneer and Admin
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Button to let user return home

        returnHome.setOnClickListener(view -> {
            Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(registerIntent);
        });
    }

    private void registerUser() {
        String email =  emailField.getText().toString();
        String firstName =  firstNameField.getText().toString();
        String lastName =  lastNameField.getText().toString();
        String password =  passwordField.getText().toString();
        String houseAddress =  houseAddressField.getText().toString();
        String role = roleSpinner.getSelectedItem().toString();



        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("first_name", firstName)
                .add("last_name", lastName)
                .add("password",password)
                .add("house_address",houseAddress)
                .add("role", role)
                .build();
        // Post user information to database
        Request request = new Request.Builder()
                .url(registerURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            // If user registration fails
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show());
            }
            // Otherwise user has been registered
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "User Registered !", Toast.LENGTH_SHORT).show());

                    Intent intent;
                    switch (role) {
                        case "admin":
                            intent = new Intent(RegisterActivity.this, AdminActivity.class);
                            break;
                        case "auctioneer":
                            intent = new Intent(RegisterActivity.this, AuctioneerActivity.class);
                            break;
                        case "bidder":
                        default:
                            intent = new Intent(RegisterActivity.this, BidderActivity.class);
                            break;
                    }
                    startActivity(intent);
                    finish();
                }
                if (!response.isSuccessful()){
                    throw new IOException("Error" + response);
                    // Creates wallet address for new user.
                    //
                    // File directory ;
                    // Using users password for encryption
                    //String walletAddress =  EthereumWallet.createEthereumWallet(password, directory);
                }
            }

            private void storeWalletAddress(String email, String walletAddress) {

            }
        });
    }

}
