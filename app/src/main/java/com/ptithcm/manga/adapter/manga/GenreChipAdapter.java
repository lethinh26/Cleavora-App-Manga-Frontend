package com.ptithcm.manga.adapter.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.GenreResponse;

import java.util.ArrayList;
import java.util.List;

public class GenreChipAdapter extends RecyclerView.Adapter<GenreChipAdapter.ViewHolder> {

    private List<GenreResponse> genres = new ArrayList<>();
    private OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(GenreResponse genre);
    }

    public void setOnGenreClickListener(OnGenreClickListener listener) {
        this.listener = listener;
    }

    public void setGenres(List<GenreResponse> genres) {
        this.genres = genres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenreResponse genre = genres.get(position);
        holder.tvGenre.setText(genre.getName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGenreClick(genre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenre = itemView.findViewById(R.id.tv_genre);
        }
    }
}
