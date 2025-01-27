package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {

    private List<Contract> contractList;

    public ContractAdapter(List<Contract> contractList) {
        this.contractList = contractList;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contract_item, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        Contract contract = contractList.get(position);
        holder.bidIdTextView.setText("Bid ID: " + contract.getBidId());
        holder.contractAddressTextView.setText("Address: " + contract.getContractAddress());
        holder.transactionHashTextView.setText("Tx Hash: " + contract.getTransactionHash());
        holder.createdAtTextView.setText("Created At: " + contract.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    static class ContractViewHolder extends RecyclerView.ViewHolder {
        TextView bidIdTextView, contractAddressTextView, transactionHashTextView, createdAtTextView;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            bidIdTextView = itemView.findViewById(R.id.bidIdTextView);
            contractAddressTextView = itemView.findViewById(R.id.contractAddressTextView);
            transactionHashTextView = itemView.findViewById(R.id.transactionHashTextView);
            createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
        }
    }
}
