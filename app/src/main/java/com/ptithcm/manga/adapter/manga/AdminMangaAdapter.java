package com.ptithcm.manga.adapter.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.MangaResponse;

import java.util.ArrayList;
import java.util.List;

public class AdminMangaAdapter extends RecyclerView.Adapter<AdminMangaAdapter.ViewHolder> {

    private List<MangaResponse> mangaList = new ArrayList<>();
    private final OnMangaActionListener listener;

    public interface OnMangaActionListener {
        void onEdit(MangaResponse manga, int position);
        void onDelete(MangaResponse manga, int position);
        void onAddChapter(MangaResponse manga, int position);
    }

    public AdminMangaAdapter(OnMangaActionListener listener) {
        this.listener = listener;
    }

    public void setMangas(List<MangaResponse> mangas) {
        this.mangaList = mangas != null ? mangas : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeManga(int position) {
        if (position >= 0 && position < mangaList.size()) {
            mangaList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_manga, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaResponse manga = mangaList.get(position);

        holder.tvTitle.setText(manga.getTitle());
        holder.tvAuthor.setText(manga.getAuthorName() != null ? manga.getAuthorName() : "N/A");
        holder.tvStatus.setText(manga.getStatus() != null ? manga.getStatus() : "ONGOING");

        Glide.with(holder.itemView.getContext())
                .load(manga.getCoverImageUrl())
                .placeholder(R.drawable.bg_placeholder_cover)
                .error(R.drawable.bg_placeholder_cover)
                .centerCrop()
                .into(holder.ivCover);

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.btnMenu);
            // Setup popup menu items programmatically since we don't have menu xml
            popupMenu.getMenu().add(0, 1, 0, "Sửa truyện");
            popupMenu.getMenu().add(0, 2, 0, "Thêm Chapter");
            popupMenu.getMenu().add(0, 3, 0, "Xóa truyện");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    switch (item.getItemId()) {
                        case 1:
                            listener.onEdit(manga, holder.getAdapterPosition());
                            return true;
                        case 2:
                            listener.onAddChapter(manga, holder.getAdapterPosition());
                            return true;
                        case 3:
                            listener.onDelete(manga, holder.getAdapterPosition());
                            return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover, btnMenu;
        TextView tvTitle, tvAuthor, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnMenu = itemView.findViewById(R.id.btn_menu);
        }
    }
}
