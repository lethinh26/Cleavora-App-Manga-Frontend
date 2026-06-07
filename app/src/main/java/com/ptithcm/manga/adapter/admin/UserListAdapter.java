package com.ptithcm.manga.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        notifyDataSetChanged();
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
        android.content.Context ctx = holder.itemView.getContext();

        // Name
        holder.tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Người dùng");

        // Email
        holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        // Role badge
        String roleText = user.getRole();
        holder.tvRole.setText(roleText != null ? roleText : "USER");
        if ("SUPERADMIN".equals(roleText)) {
            holder.tvRole.setBackgroundResource(R.drawable.bg_chip_active);
            holder.tvRole.setTextColor(ContextCompat.getColor(ctx, R.color.on_primary));
        } else if ("ADMIN".equals(roleText)) {
            holder.tvRole.setBackgroundResource(R.drawable.bg_chip_active);
            holder.tvRole.setTextColor(ContextCompat.getColor(ctx, R.color.on_primary));
        } else {
            holder.tvRole.setBackgroundResource(R.drawable.bg_chip);
            holder.tvRole.setTextColor(ContextCompat.getColor(ctx, R.color.subtext));
        }

        // Avatar
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(ctx)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.bg_placeholder_avatar)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.bg_placeholder_avatar);
        }

        // Status dot + status text
        boolean isActive = Boolean.TRUE.equals(user.getActive());
        if (holder.viewStatusDot != null) {
            holder.viewStatusDot.setBackgroundResource(
                    isActive ? R.drawable.bg_status_dot_active : R.drawable.bg_status_dot_inactive
            );
        }
        if (holder.tvStatus != null) {
            if (isActive) {
                holder.tvStatus.setText("Đang hoạt động");
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.success));
            } else {
                holder.tvStatus.setText("Đã bị khóa");
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.status_inactive));
            }
        }

        // Active switch - gỡ listener trước để tránh trigger khi bind
        if (holder.switchActive != null) {
            if ("SUPERADMIN".equals(user.getRole())) {
                holder.switchActive.setVisibility(View.GONE);
            } else {
                holder.switchActive.setVisibility(View.VISIBLE);
                holder.switchActive.setOnCheckedChangeListener(null);
                holder.switchActive.setChecked(isActive);
                holder.switchActive.setOnCheckedChangeListener((buttonView, checked) ->
                        listener.onToggleActive(user.getId()));
            }
        }

        // Change role button (SUPERADMIN only, không hiện với chính SUPERADMIN)
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
        View viewStatusDot;
        TextView tvName, tvEmail, tvRole, tvStatus;
        SwitchMaterial switchActive;
        MaterialButton btnChangeRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            viewStatusDot = itemView.findViewById(R.id.view_status_dot);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvStatus = itemView.findViewById(R.id.tv_status);
            switchActive = itemView.findViewById(R.id.switch_active);
            btnChangeRole = itemView.findViewById(R.id.btn_change_role);
        }
    }
}
