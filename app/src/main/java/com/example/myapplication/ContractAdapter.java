package com.example.myapplication;


import android.content.ClipData;
import android.content.ClipboardManager;
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

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ContractViewHolder> {

    private List<Contract> contractList;
    private Context context;
    public ContractAdapter(Context context, List<Contract> contractList) {
        this.contractList = contractList;
        this.context = context;
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

        holder.copyButton.setOnClickListener(v -> {
            copyToClipboard(contract.getContractAddress());
        });
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    static class ContractViewHolder extends RecyclerView.ViewHolder {
        TextView bidIdTextView, contractAddressTextView, transactionHashTextView, createdAtTextView;
        Button copyButton;
        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            bidIdTextView = itemView.findViewById(R.id.bidIdTextView);
            contractAddressTextView = itemView.findViewById(R.id.contractAddressTextView);
            transactionHashTextView = itemView.findViewById(R.id.transactionHashTextView);
            createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
            copyButton = itemView.findViewById(R.id.buttonCopyAddress);
        }
    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Contract Address", text);
        clipboard.setPrimaryClip(clip);
        //Toast.makeText(context, "Contract Address Copied!", Toast.LENGTH_SHORT).show();
    }

}
