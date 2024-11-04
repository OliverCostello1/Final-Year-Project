package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String TAG = "UserAdapter"; // Tag for logging
    private final List<User> userList;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (position < 0 || position >= userList.size()) {
            Log.e(TAG, "Position " + position + " is out of bounds for userList of size " + userList.size());
            return; // Exit if position is invalid
        }

        User user = userList.get(position);
        if (user == null) {
            Log.e(TAG, "User object at position " + position + " is null.");
            return; // Exit if user is null
        }

        // Set user details to the TextViews
        try {
            holder.walletAddressTextView.setText(user.getWalletAddress());
            holder.firstNameTextView.setText(user.getFirstName());
            holder.lastNameTextView.setText(user.getLastName());
            holder.roleTextView.setText(user.getRole());
        } catch (Exception e) {
            Log.e(TAG, "Error binding data for user at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0; // Safeguard for null list
    }

    public void updateData(List<User> newUserList) {
        if (newUserList == null) {
            Log.e(TAG, "New user list is null. Cannot update.");
            return; // Exit if the new list is null
        }

        Log.d(TAG, "Updating user list. Old size: " + userList.size() + ", New size: " + newUserList.size());
        this.userList.clear();
        this.userList.addAll(newUserList);
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView walletAddressTextView;
        TextView firstNameTextView;
        TextView lastNameTextView;
        TextView roleTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            walletAddressTextView = itemView.findViewById(R.id.wallet_address_admin);
            firstNameTextView = itemView.findViewById(R.id.f_name_admin);
            lastNameTextView = itemView.findViewById(R.id.l_name_admin);
            roleTextView = itemView.findViewById(R.id.role_admin);
        }
    }
}
