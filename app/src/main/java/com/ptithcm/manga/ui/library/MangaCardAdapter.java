package com.ptithcm.manga.ui.library;

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

public class MangaCardAdapter extends RecyclerView.Adapter<MangaCardAdapter.ViewHolder> {

    private List<MangaResponse> mangas = new ArrayList<>();
    private OnMangaClickListener listener;

    public interface OnMangaClickListener {
        void onMangaClick(MangaResponse manga);
    }

    public MangaCardAdapter(OnMangaClickListener listener) {
        this.listener = listener;
    }

    public void addMangas(List<MangaResponse> newMangas) {
        int startPos = mangas.size();
        mangas.addAll(newMangas);
        notifyItemRangeInserted(startPos, newMangas.size());
    }

    public void clear() {
        mangas.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manga_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaResponse manga = mangas.get(position);
        holder.tvTitle.setText(manga.getTitle());
        if (manga.getCoverImageUrl() != null && !manga.getCoverImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(manga.getCoverImageUrl())
                    .into(holder.ivCover);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMangaClick(manga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
