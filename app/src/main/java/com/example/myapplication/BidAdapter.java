package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {
    private static final String TAG = "BidAdapter";
    private final List<Bid> bids;
    private final Context context;

    public BidAdapter(List<Bid> bids, Context context) {
        this.bids = new ArrayList<>(bids != null ? bids : new ArrayList<>());
        this.context = context;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bids_item, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        Bid bid = bids.get(position);
        holder.bind(bid);
    }

    @Override
    public int getItemCount() {
        return bids.size();
    }

    public void updateData(List<Bid> newBids) {
        if (newBids != null) {
            this.bids.clear();
            this.bids.addAll(newBids);
            notifyDataSetChanged();
        } else {
            Log.e(TAG, "updateData: Provided bid list is null");
        }
    }

    public class BidViewHolder extends RecyclerView.ViewHolder {
        private final TextView bidIdText;
        private final TextView propertyIdText;
        private final TextView bidAmountText;
        private final TextView bidTimeText;
        private final TextView bidStatusText;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            bidIdText = itemView.findViewById(R.id.bid_id_label);
            propertyIdText = itemView.findViewById(R.id.property_id_label);
            bidAmountText = itemView.findViewById(R.id.bid_amount_label);
            bidTimeText = itemView.findViewById(R.id.bid_time_label);
            bidStatusText = itemView.findViewById(R.id.bid_status_label);
        }

        public void bind(Bid bid) {
            bidIdText.setText(context.getString(R.string.bid_id, bid.getBid_id()));
            propertyIdText.setText(context.getString(R.string.property_id_text, bid.getPropertyID()));
            bidAmountText.setText(context.getString(R.string.bid_amount, bid.getBid_amount()));
            bidTimeText.setText(context.getString(R.string.bid_time, bid.getFormattedTimeStamp() ));
            bidStatusText.setText(context.getString(R.string.bid_status, bid.getBid_status()));
        }
    }
}
