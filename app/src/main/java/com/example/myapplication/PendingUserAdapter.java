package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PendingUserAdapter extends RecyclerView.Adapter<PendingUserAdapter.UserViewHolder> {
    private final List<User> userList;
    private final Context context;
    private final OnApproveClickListener approveClickListener;

    public PendingUserAdapter(List<User> userList, Context context, OnApproveClickListener approveClickListener) {
        this.userList = userList;
        this.context = context;
        this.approveClickListener = approveClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_pending_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userIDTextView.setText(context.getString(R.string.user_id_label, user.getId()));  // userId is a String
        holder.walletAddressTextView.setText(context.getString(R.string.wallet_address, user.getWalletAddress()));
        holder.firstNameTextView.setText(context.getString(R.string.f_name, user.getFirstName()));
        holder.lastNameTextView.setText(context.getString(R.string.l_name, user.getLastName()));
        holder.roleTextView.setText(context.getString(R.string.role_string, user.getRole()));
        holder.approveButton.setText(context.getString(R.string.approve_user, user.getId()));

        // Set click listener for the approve button
        holder.approveButton.setOnClickListener(v -> {
            if (approveClickListener != null) {
                approveClickListener.onApproveClick(user.getId(), position);  // pass userId as String
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<User> newUserList) {
        this.userList.clear();
        this.userList.addAll(newUserList);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView walletAddressTextView;
        TextView userIDTextView;
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView roleTextView;
        Button approveButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIDTextView = itemView.findViewById(R.id.user_id);
            walletAddressTextView = itemView.findViewById(R.id.wallet_address_admin);
            firstNameTextView = itemView.findViewById(R.id.f_name_admin);
            lastNameTextView = itemView.findViewById(R.id.l_name_admin);
            roleTextView = itemView.findViewById(R.id.role_admin);
            approveButton = itemView.findViewById(R.id.approve_user_button);
        }
    }

    // Interface for approve button click handling
    public interface OnApproveClickListener {
        void onApproveClick(String userId, int position);  // userId is now a String
    }
}
