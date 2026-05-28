package com.ptithcm.manga.adapter.manga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.ChapterResponse;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<ChapterResponse> chapterList = new ArrayList<>();
    private final OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(ChapterResponse chapter);
    }

    public ChapterAdapter(OnChapterClickListener listener) {
        this.listener = listener;
    }

    public void setChapters(List<ChapterResponse> chapters) {
        this.chapterList = chapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        ChapterResponse chapter = chapterList.get(position);
        
        holder.tvChapterNumber.setText("Chương " + chapter.getChapterNumber());
        if (chapter.getTitle() != null && !chapter.getTitle().isEmpty()) {
            holder.tvChapterTitle.setText(chapter.getTitle());
            holder.tvChapterTitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvChapterTitle.setVisibility(View.GONE);
        }
        
        // Simple date formatting (can be improved based on actual string format)
        String dateStr = chapter.getCreatedAt() != null ? chapter.getCreatedAt().substring(0, 10) : "";
        holder.tvDate.setText(dateStr);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChapterClick(chapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList != null ? chapterList.size() : 0;
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterNumber;
        TextView tvChapterTitle;
        TextView tvDate;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterNumber = itemView.findViewById(R.id.tv_chapter_number);
            tvChapterTitle = itemView.findViewById(R.id.tv_chapter_title);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
