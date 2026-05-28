package com.ptithcm.manga.data.model.response;

public class ChapterImageResponse {
    private Long id;
    private String imageUrl;
    private Integer pageNumber;

    public ChapterImageResponse() {
    }

    public ChapterImageResponse(Long id, String imageUrl, Integer pageNumber) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.pageNumber = pageNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
