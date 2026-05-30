package com.ptithcm.manga.adapter.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.Genre;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenreSelectAdapter extends RecyclerView.Adapter<GenreSelectAdapter.ViewHolder> {

    private List<Genre> genres = new ArrayList<>();
    private Set<Integer> selectedGenreIds = new HashSet<>();

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedGenreIds() {
        return selectedGenreIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We reuse the basic Android checkbox layout or item_genre_chip if it supports checkable
        // For simplicity, we just use a CheckBox if there's no custom layout
        CheckBox cb = new CheckBox(parent.getContext());
        cb.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(cb);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.cbGenre.setText(genre.getName());
        holder.cbGenre.setOnCheckedChangeListener(null);
        holder.cbGenre.setChecked(selectedGenreIds.contains(genre.getId()));
        holder.cbGenre.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGenreIds.add(genre.getId());
            } else {
                selectedGenreIds.remove(genre.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbGenre = (CheckBox) itemView;
        }
    }
}
