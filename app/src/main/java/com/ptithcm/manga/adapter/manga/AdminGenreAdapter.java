package com.ptithcm.manga.adapter.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class AdminGenreAdapter extends RecyclerView.Adapter<AdminGenreAdapter.ViewHolder> {

    private List<Genre> genreList = new ArrayList<>();
    private final OnGenreActionListener listener;

    public interface OnGenreActionListener {
        void onEdit(Genre genre, int position);
        void onDelete(Genre genre, int position);
    }

    public AdminGenreAdapter(OnGenreActionListener listener) {
        this.listener = listener;
    }

    public void setGenres(List<Genre> genres) {
        this.genreList = genres != null ? genres : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addGenre(Genre genre) {
        this.genreList.add(genre);
        notifyItemInserted(this.genreList.size() - 1);
    }

    public void updateGenre(int position, Genre genre) {
        if (position >= 0 && position < genreList.size()) {
            genreList.set(position, genre);
            notifyItemChanged(position);
        }
    }

    public void removeGenre(int position) {
        if (position >= 0 && position < genreList.size()) {
            genreList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genreList.get(position);

        holder.tvGenreName.setText(genre.getName());
        holder.tvGenreSlug.setText(genre.getSlug());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(genre, holder.getAdapterPosition());
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(genre, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGenreName, tvGenreSlug;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenreName = itemView.findViewById(R.id.tv_genre_name);
            tvGenreSlug = itemView.findViewById(R.id.tv_genre_slug);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
