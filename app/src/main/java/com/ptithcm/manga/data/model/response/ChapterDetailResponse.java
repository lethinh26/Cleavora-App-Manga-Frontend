package com.ptithcm.manga.data.model.response;

import java.util.List;

public class ChapterDetailResponse {
    private Integer id;
    private String title;
    private Double chapterNumber;
    private Integer viewCount;
    private String createdAt;
    private String updatedAt;
    private List<ChapterImageResponse> images;

    public ChapterDetailResponse() {
    }

    public ChapterDetailResponse(Integer id, String title, Double chapterNumber, Integer viewCount, String createdAt, String updatedAt, List<ChapterImageResponse> images) {
        this.id = id;
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
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

    public List<ChapterImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ChapterImageResponse> images) {
        this.images = images;
    }
}
