package com.ptithcm.manga.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.GenreResponse;

import java.util.ArrayList;
import java.util.List;

public class GenreManageAdapter extends RecyclerView.Adapter<GenreManageAdapter.ViewHolder> {

    private List<GenreResponse> items = new ArrayList<>();
    private final GenreManageListener listener;

    public interface GenreManageListener {
        void onEdit(GenreResponse genre);
        void onDelete(GenreResponse genre);
    }

    public GenreManageAdapter(GenreManageListener listener) {
        this.listener = listener;
    }

    public void setItems(List<GenreResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenreResponse genre = items.get(position);
        holder.tvName.setText(genre.getName());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(genre));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(genre));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_genre_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
