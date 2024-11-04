package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
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
        User user = userList.get(position);
        holder.walletAddressTextView.setText(user.getWalletAddress());
        holder.firstNameTextView.setText(user.getFirstName());
        holder.lastNameTextView.setText(user.getLastName());
        holder.roleTextView.setText(user.getRole());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
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
