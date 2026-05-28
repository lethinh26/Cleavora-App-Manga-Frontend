package com.ptithcm.manga.adapter.manga;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.ChapterImageResponse;

import java.util.ArrayList;
import java.util.List;

public class ReaderAdapter extends RecyclerView.Adapter<ReaderAdapter.ReaderViewHolder> {

    private List<ChapterImageResponse> imageList = new ArrayList<>();
    private final Context context;
    private final OnImageClickListener clickListener;

    public interface OnImageClickListener {
        void onImageClick();
    }

    public ReaderAdapter(Context context, OnImageClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    public void setImages(List<ChapterImageResponse> images) {
        this.imageList = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reader_image, parent, false);
        return new ReaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReaderViewHolder holder, int position) {
        ChapterImageResponse imageInfo = imageList.get(position);
        
        holder.progressBar.setVisibility(View.VISIBLE);
        
        Glide.with(context)
                .load(imageInfo.getImageUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivPage);

        holder.ivPage.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onImageClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList != null ? imageList.size() : 0;
    }

    static class ReaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPage;
        ProgressBar progressBar;

        public ReaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPage = itemView.findViewById(R.id.iv_page);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
