package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {
    private static final String TAG = "PropertyAdapter";
    private List<Property> properties;
    private static Context context = null;

    public PropertyAdapter(List<Property> properties, Context context) {
        this.properties = new ArrayList<>(properties != null ? properties : new ArrayList<>());
        this.context = context;
        Log.d(TAG, "Adapter initialized with " + this.properties.size() + " properties");
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        Log.d(TAG, "Created new ViewHolder");
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.setPropertyData(property);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void updateData(List<Property> newProperties) {
        if (newProperties == null) {
            Log.e(TAG, "Attempted to update with null property list");
            return;
        }

        this.properties = new ArrayList<>(newProperties);
        Log.d(TAG, "Updating adapter with " + properties.size() + " properties");
        notifyDataSetChanged();
    }

    public Property getPropertyAt(int position) {
        return properties.get(position);
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        public TextView propertyIdTextView;
        public TextView eircodeTextView;
        public TextView linkTextView;
        public TextView auctioneerIdTextView;
        public TextView askingPriceTextView;
        public TextView currentBidTextView;
        public EditText enterBidView;
        public Button submitBidButton;
        private Property property;
        private SharedPreferences sharedPreferences;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyIdTextView = itemView.findViewById(R.id.property_id);
            eircodeTextView = itemView.findViewById(R.id.eircode);
            linkTextView = itemView.findViewById(R.id.link);
            auctioneerIdTextView = itemView.findViewById(R.id.auctioneer_id);
            askingPriceTextView = itemView.findViewById(R.id.asking_price);
            currentBidTextView = itemView.findViewById(R.id.current_bid);
            enterBidView = itemView.findViewById(R.id.enter_bid);
            submitBidButton = itemView.findViewById(R.id.submit_bid);

            // Get the shared preferences instance
            sharedPreferences = itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

            // Set the onClick listener for the submit bid button
            submitBidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bidAmount = enterBidView.getText().toString();
                    if (!TextUtils.isEmpty(bidAmount)) {
                        String userId = sharedPreferences.getString("user_id", "");
                        String userWallet = sharedPreferences.getString("wallet_address", "");
                        if (!userId.isEmpty() && !userWallet.isEmpty()) {
                            ((PlaceBidActivity) itemView.getContext()).submitBid(
                                    property, userId, userWallet, Double.parseDouble(bidAmount));
                        } else {
                            Toast.makeText(itemView.getContext(), "Please log in to place a bid.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "Please enter a bid amount.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setPropertyData(Property property) {
            this.property = property;
            propertyIdTextView.setText(context.getString(R.string.property_id_label, String.valueOf(property.getPropertyId())));
            eircodeTextView.setText(context.getString(R.string.eircode_label, property.getEircode()));
            linkTextView.setText(context.getString(R.string.link_label, property.getLink()));
            auctioneerIdTextView.setText(context.getString(R.string.auctioneer_id_format, String.valueOf(property.getAuctioneer_id())));
            askingPriceTextView.setText(context.getString(R.string.asking_price_format, String.valueOf(property.getAsking_price())));
            currentBidTextView.setText(context.getString(R.string.current_bid_format, String.valueOf(property.getCurrent_bid())));
        }
    }
}