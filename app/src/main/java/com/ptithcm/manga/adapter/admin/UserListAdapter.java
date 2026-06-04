package com.ptithcm.manga.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<UserResponse> items = new ArrayList<>();
    private final UserListListener listener;
    private final boolean isSuperAdmin;

    public interface UserListListener {
        void onToggleActive(int userId);
        void onChangeRole(int userId);
    }

    public UserListAdapter(UserListListener listener, boolean isSuperAdmin) {
        this.listener = listener;
        this.isSuperAdmin = isSuperAdmin;
    }

    public void setItems(List<UserResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateItem(UserResponse updated) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == updated.getId()) {
                items.set(i, updated);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponse user = items.get(position);

        holder.tvName.setText(user.getDisplayName());
        holder.tvEmail.setText(user.getEmail());

        // Role badge
        String roleText = user.getRole();
        holder.tvRole.setText(roleText != null ? roleText : "USER");
        if ("SUPERADMIN".equals(roleText)) {
            holder.tvRole.setBackgroundResource(R.drawable.bg_chip_active);
        } else if ("ADMIN".equals(roleText)) {
            holder.tvRole.setBackgroundResource(R.drawable.bg_chip_active);
        } else {
            holder.tvRole.setBackgroundResource(R.drawable.bg_chip);
        }

        // Avatar
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(holder.ivAvatar.getContext())
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.bg_placeholder_avatar)
                    .into(holder.ivAvatar);
        }

        // Active switch
        if (holder.switchActive != null) {
            holder.switchActive.setOnCheckedChangeListener(null);
            holder.switchActive.setChecked(user.getActive() != null && user.getActive());
            holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) ->
                    listener.onToggleActive(user.getId()));
        }

        // Role change button (only SUPERADMIN)
        if (holder.btnChangeRole != null) {
            if (isSuperAdmin && !"SUPERADMIN".equals(user.getRole())) {
                holder.btnChangeRole.setVisibility(View.VISIBLE);
                holder.btnChangeRole.setOnClickListener(v -> listener.onChangeRole(user.getId()));
            } else {
                holder.btnChangeRole.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvEmail, tvRole;
        Switch switchActive;
        TextView btnChangeRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
            switchActive = itemView.findViewById(R.id.switch_active);
            btnChangeRole = itemView.findViewById(R.id.btn_change_role);
        }
    }
}
