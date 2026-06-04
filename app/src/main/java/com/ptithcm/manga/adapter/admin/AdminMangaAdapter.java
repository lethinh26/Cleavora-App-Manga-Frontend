package com.ptithcm.manga.adapter.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.MangaResponse;

import java.util.ArrayList;
import java.util.List;

public class AdminMangaAdapter extends RecyclerView.Adapter<AdminMangaAdapter.ViewHolder> {

    private List<MangaResponse> items = new ArrayList<>();
    private final AdminMangaListener listener;

    public interface AdminMangaListener {
        void onClick(int mangaId);
        void onDelete(int mangaId);
        void onManageChapters(int mangaId);
    }

    public AdminMangaAdapter(AdminMangaListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MangaResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeItem(int mangaId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(mangaId)) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_manga, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaResponse manga = items.get(position);

        holder.tvTitle.setText(manga.getTitle());
        holder.tvAuthor.setText(manga.getAuthorName() != null ? manga.getAuthorName() : "");
        holder.tvCreatedAt.setText(manga.getCreatedAt() != null ? manga.getCreatedAt() : "");

        // Approval status badge
        if (manga.getApprovalStatus() != null) {
            holder.tvApprovalStatus.setVisibility(View.VISIBLE);
            switch (manga.getApprovalStatus()) {
                case APPROVED:
                    holder.tvApprovalStatus.setText("Đã duyệt");
                    holder.tvApprovalStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
                    break;
                case PENDING:
                    holder.tvApprovalStatus.setText("Chờ duyệt");
                    holder.tvApprovalStatus.setBackgroundColor(Color.parseColor("#FF9800"));
                    break;
                case REJECTED:
                    holder.tvApprovalStatus.setText("Từ chối");
                    holder.tvApprovalStatus.setBackgroundColor(Color.parseColor("#F44336"));
                    break;
            }
        } else {
            holder.tvApprovalStatus.setVisibility(View.GONE);
        }

        // Cover
        if (manga.getCoverImageUrl() != null && !manga.getCoverImageUrl().isEmpty()) {
            Glide.with(holder.ivCover.getContext())
                    .load(manga.getCoverImageUrl())
                    .placeholder(R.drawable.bg_placeholder_cover)
                    .into(holder.ivCover);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(manga.getId()));

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(manga.getId()));
        holder.btnChapters.setOnClickListener(v -> listener.onManageChapters(manga.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvCreatedAt, tvApprovalStatus;
        View btnDelete, btnChapters;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            tvApprovalStatus = itemView.findViewById(R.id.tv_approval_status);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnChapters = itemView.findViewById(R.id.btn_chapters);
        }
    }
}
