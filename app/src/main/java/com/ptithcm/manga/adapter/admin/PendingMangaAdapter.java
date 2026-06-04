package com.ptithcm.manga.adapter.admin;

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

public class PendingMangaAdapter extends RecyclerView.Adapter<PendingMangaAdapter.ViewHolder> {

    private List<MangaResponse> items = new ArrayList<>();
    private final PendingMangaListener listener;

    public interface PendingMangaListener {
        void onApprove(int mangaId);
        void onReject(int mangaId);
    }

    public PendingMangaAdapter(PendingMangaListener listener) {
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
                .inflate(R.layout.item_manga_pending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaResponse manga = items.get(position);
        holder.tvTitle.setText(manga.getTitle());
        holder.tvSubmittedAt.setText(manga.getCreatedAt() != null ? manga.getCreatedAt() : "");

        if (manga.getCoverImageUrl() != null && !manga.getCoverImageUrl().isEmpty()) {
            Glide.with(holder.ivCover.getContext())
                    .load(manga.getCoverImageUrl())
                    .placeholder(R.drawable.bg_placeholder_cover)
                    .into(holder.ivCover);
        }

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(manga.getId()));
        holder.btnReject.setOnClickListener(v -> listener.onReject(manga.getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvSubmittedAt;
        View btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubmittedAt = itemView.findViewById(R.id.tv_submitted_at);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
