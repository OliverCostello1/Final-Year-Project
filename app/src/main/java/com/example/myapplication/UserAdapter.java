package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final Context context;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String id, int position);
    }

    public UserAdapter(List<User> userList, Context context, OnDeleteClickListener deleteClickListener) {
        this.userList = userList;
        this.context = context;
        this.deleteClickListener = deleteClickListener;
        Log.d("UserAdapter", "Adapter initialized with userList size: " + userList.size());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("UserAdapter", "Creating ViewHolder");

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Log.d("UserAdapter", "Binding ViewHolder at position: " + position);

        User user = userList.get(position);
        holder.userIDTextView.setText(context.getString(R.string.user_id_label, user.getID()));
        holder.walletAddressTextView.setText(context.getString(R.string.wallet_address, user.getWalletAddress()));
        holder.firstNameTextView.setText(context.getString(R.string.f_name, user.getFirstName()));
        holder.lastNameTextView.setText(context.getString(R.string.l_name, user.getLastName()));
        holder.roleTextView.setText(context.getString(R.string.role_string, user.getRole()));
        holder.deleteUserButton.setText(context.getString(R.string.delete_user_text, user.getID()));

        holder.deleteUserButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                // Call deleteClickListener with the user's ID instead of wallet address
                deleteClickListener.onDeleteClick(String.valueOf(user.getID()), position);
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
        Button deleteUserButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userIDTextView = itemView.findViewById(R.id.user_id);
            walletAddressTextView = itemView.findViewById(R.id.wallet_address_admin);
            firstNameTextView = itemView.findViewById(R.id.f_name_admin);
            lastNameTextView = itemView.findViewById(R.id.l_name_admin);
            roleTextView = itemView.findViewById(R.id.role_admin);
            deleteUserButton = itemView.findViewById(R.id.delete_user_button);
            Log.d("UserViewHolder", "Views initialized: " + (deleteUserButton != null));
        }
    }
}
