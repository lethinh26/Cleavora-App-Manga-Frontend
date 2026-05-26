package com.ptithcm.manga.adapter.genre;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.GenreResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiSelectGenreAdapter extends RecyclerView.Adapter<MultiSelectGenreAdapter.ViewHolder> {

    private List<GenreResponse> genres = new ArrayList<>();
    private final Set<Integer> selectedIds = new HashSet<>();

    public void setGenres(List<GenreResponse> genres) {
        this.genres = genres;
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenreResponse genre = genres.get(position);
        holder.tvGenreName.setText(genre.getName());

        boolean isSelected = selectedIds.contains(genre.getId());
        
        if (isSelected) {
            holder.tvGenreName.setBackgroundResource(R.drawable.bg_chip_active);
            holder.tvGenreName.setTextColor(Color.WHITE);
        } else {
            holder.tvGenreName.setBackgroundResource(R.drawable.bg_chip);
            holder.tvGenreName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text));
        }

        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                selectedIds.remove(genre.getId());
            } else {
                selectedIds.add(genre.getId());
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGenreName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenreName = itemView.findViewById(R.id.tv_genre_name);
        }
    }
}
