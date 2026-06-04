package com.ptithcm.manga.adapter.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.MangaResponse;

import java.util.ArrayList;
import java.util.List;

public class AdminPendingAdapter extends RecyclerView.Adapter<AdminPendingAdapter.ViewHolder> {

    private List<MangaResponse> mangaList = new ArrayList<>();
    private final OnPendingActionListener listener;

    public interface OnPendingActionListener {
        void onApprove(MangaResponse manga, int position);
        void onReject(MangaResponse manga, int position);
    }

    public AdminPendingAdapter(OnPendingActionListener listener) {
        this.listener = listener;
    }

    public void setMangas(List<MangaResponse> mangas) {
        this.mangaList = mangas != null ? mangas : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mangaList.size()) {
            mangaList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manga_pending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaResponse manga = mangaList.get(position);

        holder.tvTitle.setText(manga.getTitle());
        holder.tvSubmittedBy.setText("Người đăng: " + (manga.getAuthorName() != null ? manga.getAuthorName() : "N/A"));

        String dateStr = manga.getCreatedAt() != null ? manga.getCreatedAt().substring(0, 10) : "";
        holder.tvSubmittedAt.setText(dateStr);

        Glide.with(holder.itemView.getContext())
                .load(manga.getCoverImageUrl())
                .placeholder(R.drawable.bg_placeholder_cover)
                .error(R.drawable.bg_placeholder_cover)
                .centerCrop()
                .into(holder.ivCover);

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(manga, holder.getAdapterPosition());
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(manga, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvSubmittedBy, tvSubmittedAt;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubmittedBy = itemView.findViewById(R.id.tv_submitted_by);
            tvSubmittedAt = itemView.findViewById(R.id.tv_submitted_at);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
