package com.ptithcm.manga.data.model.request;

import java.util.List;

public class ChapterRequest {
    private String title;
    private double chapterNumber;
    private List<String> imageUrls;

    public ChapterRequest() {}

    public ChapterRequest(String title, double chapterNumber, List<String> imageUrls) {
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.imageUrls = imageUrls;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(double chapterNumber) { this.chapterNumber = chapterNumber; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
