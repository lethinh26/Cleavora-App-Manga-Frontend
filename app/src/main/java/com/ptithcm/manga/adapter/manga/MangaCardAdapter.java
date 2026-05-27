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
import com.ptithcm.manga.data.model.response.MangaResponse;

import java.util.List;

public class MangaCardAdapter extends RecyclerView.Adapter<MangaCardAdapter.MangaViewHolder> {
    private List<MangaResponse> mangaList;
    private OnMangaClickListener listener;

    public interface OnMangaClickListener {
        void onMangaClick(MangaResponse manga);
    }

    public MangaCardAdapter(List<MangaResponse> mangaList, OnMangaClickListener listener){
        this.mangaList = mangaList;
        this.listener = listener;
    }

    public void updateData(List<MangaResponse> newList) {
        this.mangaList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MangaCardAdapter.MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manga_card, parent, false);
        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaCardAdapter.MangaViewHolder holder, int position) {
        MangaResponse currentManga = mangaList.get(position);
        holder.tvTitle.setText(currentManga.getTitle());

        Glide.with(holder.itemView.getContext())
                .load(currentManga.getCoverImageUrl())
                .placeholder(R.drawable.bg_placeholder_cover)
                .error(R.drawable.bg_placeholder_cover)
                .centerCrop()
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMangaClick(currentManga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangaList != null ? mangaList.size() : 0;
    }

    public static class MangaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;

        public MangaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
