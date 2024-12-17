package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PropertyAdapter2 extends RecyclerView.Adapter<PropertyAdapter2.PropertyViewHolder> {
    private Context context ;
    private final List<Property> propertyList;


    public PropertyAdapter2(Context context, List<Property> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_item2, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        // Bind data to views
        holder.propertyIdTextView.setText("Property ID: " + property.getPropertyId());
        holder.eircodeTextView.setText("Eircode: " + property.getEircode());
        holder.linkTextView.setText("Link: " + property.getLink());
        holder.auctioneerIdTextView.setText("Auctioneer ID: " + property.getAuctioneer_id());
        holder.askingPriceTextView.setText("Asking Price: €" + property.getAsking_price());
        holder.currentBidTextView.setText("Current Bid: €" + property.getCurrent_bid());
        holder.auctioneerWalletTextView.setText("Auctioneer Wallet: " + property.getAuctioneer_wallet());

    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    // ViewHolder class
    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView propertyIdTextView, eircodeTextView, linkTextView, auctioneerIdTextView,
                askingPriceTextView, currentBidTextView, auctioneerWalletTextView;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            propertyIdTextView = itemView.findViewById(R.id.propertyIdText);
            eircodeTextView = itemView.findViewById(R.id.eircodeText);
            linkTextView = itemView.findViewById(R.id.linkText);
            auctioneerIdTextView = itemView.findViewById(R.id.auctioneer_id);
            askingPriceTextView = itemView.findViewById(R.id.askingPriceText);
            currentBidTextView = itemView.findViewById(R.id.currentBidText);
            auctioneerWalletTextView = itemView.findViewById(R.id.auctioneerWallet);
        }
    }
}
