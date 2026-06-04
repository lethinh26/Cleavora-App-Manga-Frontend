package com.ptithcm.manga.adapter.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private List<UserResponse> userList = new ArrayList<>();
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserClick(UserResponse user, int position);
    }

    public AdminUserAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<UserResponse> users) {
        this.userList = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateItem(int position, UserResponse user) {
        if (position >= 0 && position < userList.size()) {
            userList.set(position, user);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponse user = userList.get(position);

        holder.tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "N/A");
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText(user.getRole());

        // Hiển thị trạng thái active
        Boolean active = user.getActive();
        if (active != null && !active) {
            holder.tvName.setAlpha(0.5f);
            holder.tvEmail.setAlpha(0.5f);
            holder.tvRole.setText(user.getRole() + " (Bị khóa)");
        } else {
            holder.tvName.setAlpha(1.0f);
            holder.tvEmail.setAlpha(1.0f);
        }

        Glide.with(holder.itemView.getContext())
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.bg_placeholder_avatar)
                .error(R.drawable.bg_placeholder_avatar)
                .circleCrop()
                .into(holder.ivAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onUserClick(user, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvEmail, tvRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
        }
    }
}
