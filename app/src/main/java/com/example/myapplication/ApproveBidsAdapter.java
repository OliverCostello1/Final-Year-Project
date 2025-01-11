/*package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApproveBidsAdapter extends RecyclerView.Adapter<ApproveBidsAdapter.BidViewHolder> {
    private List<Bid> bidList;
    private Context context;

    public ApproveBidsAdapter(List<Bid> bidList, Context context) {
        this.bidList = bidList;
        this.context = context;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.approve_bids_item, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        Bid bid = bidList.get(position);

        // Bind data to the views
        holder.propertyId.setText(holder.itemView.getResources().getString(R.string.property_id_format, bid.getPropertyID()));
        holder.bidAmount.setText(holder.itemView.getResources().getString (R.string.bid_amount , bid.getBid_amount()));
        holder.bidTime.setText(holder.itemView.getResources().getString(R.string.time,  bid.getTime_stamp()));
        holder.bidderWallet.setText(holder.itemView.getResources().getString(R.string.bidder_wallet, bid.getBidder_wallet()));
        holder.bidID.setText(holder.itemView.getResources().getString(R.string.bid_id, bid.getBid_id()));
        holder.bidStatus.setText(holder.itemView.getResources().getString(R.string.bid_status,  bid.getBid_status()));
        holder.approveButton.setOnClickListener(v -> {
            if (context instanceof ApproveBidsActivity) {
                ((ApproveBidsActivity) context).approveBid(bid.getBidder_id());
            }

            if (bid.getBid_status().equals("pending")) {
                ((ApproveBidsActivity) context).approveBid(bid.getBid_id());
            } else {
                Toast.makeText(context, "This bid is already approved", Toast.LENGTH_SHORT).show();
            }

        });



        // Prevent approval of already approved bids
        holder.approveButton.setEnabled(!bid.getBid_status().equals("approved"));

        holder.approveButton.setOnClickListener(v -> {
            if (bid.getBid_status().equals("pending")) {
                ((ApproveBidsActivity) context).approveBid(bid.getBid_id());
            } else {
                Toast.makeText(context, "This bid is already approved", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    static class BidViewHolder extends RecyclerView.ViewHolder {
        TextView propertyId, bidAmount, bidTime, bidderWallet, bidID, bidStatus;
        Button approveButton;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            bidID = itemView.findViewById(R.id.bid_id_text);
            propertyId = itemView.findViewById(R.id.property_id_text);
            bidAmount = itemView.findViewById(R.id.bid_amount_text);
            bidTime = itemView.findViewById(R.id.bid_time_text);
            bidderWallet = itemView.findViewById(R.id.bidder_wallet_text);
            approveButton = itemView.findViewById(R.id.approve_button);
            bidStatus = itemView.findViewById(R.id.bid_status);

        }
    }
}
*/