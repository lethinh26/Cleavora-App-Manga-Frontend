package com.ptithcm.manga.data.model.response;

public class ChapterResponse {
    private Integer id;
    private String title;
    private Double chapterNumber;
    private Integer viewCount;
    private String createdAt;
    private String updatedAt;

    public ChapterResponse() {
    }

    public ChapterResponse(Integer id, String title, Double chapterNumber, Integer viewCount, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Double chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
