package com.ptithcm.manga.adapter.library;

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
import com.ptithcm.manga.data.model.response.ReadingHistoryResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ReadingHistoryResponse> historyList;
    private final OnHistoryActionListener listener;

    private static final SimpleDateFormat[] DATE_FORMATS = {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()),
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    };

    public interface OnHistoryActionListener {
        void onContinueClick(ReadingHistoryResponse item);
        void onItemClick(ReadingHistoryResponse item);
    }

    public HistoryAdapter(OnHistoryActionListener listener) {
        this.listener = listener;
    }

    public void setList(List<ReadingHistoryResponse> list) {
        this.historyList = list;
        notifyDataSetChanged();
    }

    public List<ReadingHistoryResponse> getList() {
        return historyList;
    }

    public void clearList() {
        if (this.historyList != null) {
            this.historyList.clear();
            notifyDataSetChanged();
        }
    }

    public ReadingHistoryResponse getItem(int position) {
        if (historyList != null && position >= 0 && position < historyList.size()) {
            return historyList.get(position);
        }
        return null;
    }

    public void removeItem(int position) {
        if (historyList != null && position >= 0 && position < historyList.size()) {
            historyList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ReadingHistoryResponse item = historyList.get(position);

        holder.tvTitle.setText(item.getMangaTitle() != null ? item.getMangaTitle() : "Truyện ID: " + item.getMangaId());

        // B7: Handle null chapterId (deleted chapter)
        Integer chId = item.getChapterId();
        String chapterInfo;
        if (chId == null || chId == 0) {
            chapterInfo = "Chapter không khả dụng";
        } else {
            // Dùng chapterNumber (số chương thực) nếu có, fallback về chapterId
            String chLabel = item.getChapterNumber() != null
                    ? com.ptithcm.manga.util.ChapterFormatter.format(item.getChapterNumber())
                    : String.valueOf(chId);
            chapterInfo = "Chương " + chLabel;
            if (item.getLastPage() > 0) {
                chapterInfo += " - Trang " + item.getLastPage();
            }
        }
        holder.tvChapterInfo.setText(chapterInfo);

        holder.tvTimeAgo.setText(formatTimeAgo(item.getLastReadAt()));

        Glide.with(holder.itemView.getContext())
                .load(item.getMangaCoverUrl())
                .placeholder(R.drawable.bg_placeholder_cover)
                .error(R.drawable.bg_placeholder_cover)
                .centerCrop()
                .into(holder.ivCover);

        holder.btnContinue.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContinueClick(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    // B8: Robust date parsing with multiple format fallbacks
    private String formatTimeAgo(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "Gần đây";

        Date date = null;
        for (SimpleDateFormat sdf : DATE_FORMATS) {
            try {
                date = sdf.parse(dateString);
                if (date != null) break;
            } catch (ParseException ignored) {
                // Try next format
            }
        }

        if (date == null) return dateString;

        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;

        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 30) {
            return days + " ngày trước";
        } else {
            SimpleDateFormat outSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outSdf.format(date);
        }
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvChapterInfo, tvTimeAgo;
        MaterialButton btnContinue;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvChapterInfo = itemView.findViewById(R.id.tv_chapter_info);
            tvTimeAgo = itemView.findViewById(R.id.tv_time_ago);
            btnContinue = itemView.findViewById(R.id.btn_continue);
        }
    }
}
