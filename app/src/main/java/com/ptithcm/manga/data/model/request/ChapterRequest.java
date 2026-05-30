package com.ptithcm.manga.data.model.request;

import java.util.List;

public class ChapterRequest {
    private float chapterIndex;
    private String title;
    private List<String> imageUrls;

    public float getChapterIndex() { return chapterIndex; }
    public void setChapterIndex(float chapterIndex) { this.chapterIndex = chapterIndex; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
