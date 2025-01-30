package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ContractDetailsActivity extends AppCompatActivity {

    private EditText editTextContractAddress;
    private Button buttonFetchDetails;
    private TextView textViewDetails;
    private Web3j web3j;
    private static final String INFURA_URL = "https://holesky.infura.io/v3/0a36529195e040c99d49343061af9b0f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_details);

        editTextContractAddress = findViewById(R.id.editTextContractAddress);
        buttonFetchDetails = findViewById(R.id.buttonFetchDetails);
        textViewDetails = findViewById(R.id.textViewDetails);

        web3j = Web3j.build(new HttpService(INFURA_URL));

        buttonFetchDetails.setOnClickListener(v -> {
            String contractAddress = editTextContractAddress.getText().toString().trim();
            if (TextUtils.isEmpty(contractAddress)) {
                Toast.makeText(ContractDetailsActivity.this, "Enter a contract address", Toast.LENGTH_SHORT).show();
            } else {
                fetchContractDetails(contractAddress);
            }
        });


        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void fetchContractDetails(String contractAddress) {
        new Thread(() -> {
            try {
                Function function = new Function(
                        "getBidDetails",
                        Arrays.asList(),
                        Arrays.asList(
                                new org.web3j.abi.TypeReference<Address>() {},
                                new org.web3j.abi.TypeReference<Address>() {},
                                new org.web3j.abi.TypeReference<Utf8String>() {},
                                new org.web3j.abi.TypeReference<Uint256>() {},
                                new org.web3j.abi.TypeReference<Uint256>() {}
                        )
                );

                String encodedFunction = FunctionEncoder.encode(function);
                Transaction transaction = Transaction.createEthCallTransaction(
                        null, contractAddress, encodedFunction);

                EthCall response = web3j.ethCall(transaction, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();
                List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

                String bidderWallet = decoded.get(0).getValue().toString();
                String auctioneerWallet = decoded.get(1).getValue().toString();
                String propertyId = decoded.get(2).getValue().toString();
                BigInteger bidAmount = (BigInteger) decoded.get(3).getValue();
                BigInteger timestamp = (BigInteger) decoded.get(4).getValue();

                // Directly use the bidAmount as it is without converting to Ether
                String formattedBidAmount = bidAmount.toString();  // Regular number, no conversion to ETH

                String formattedTimestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date(timestamp.longValue() * 1000));

                runOnUiThread(() -> {
                    String details = "Bidder: " + bidderWallet + "\n" +
                            "Auctioneer: " + auctioneerWallet + "\n" +
                            "Property ID: " + propertyId + "\n" +
                            "Bid Amount: " + formattedBidAmount + "\n" +  // Displaying as a regular number
                            "Timestamp: " + formattedTimestamp;
                    textViewDetails.setText(details);
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(ContractDetailsActivity.this, "Error fetching contract details", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

}
