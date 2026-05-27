package com.ptithcm.manga.data.model.request;

import com.ptithcm.manga.data.model.response.MangaResponse;

import java.util.Set;

public class MangaSubmitRequest {
    private String title;
    private String slug;
    private String description;
    private String coverImageUrl;
    private String authorName;
    private String artistName;
    private MangaResponse.MangaStatus status;
    private MangaResponse.ApprovalStatus approvalStatus;
    private Set<Integer> genreIds;

    public MangaSubmitRequest() {
    }

    public MangaSubmitRequest(String title, String slug, String description, String coverImageUrl, String authorName, String artistName, MangaResponse.MangaStatus status, MangaResponse.ApprovalStatus approvalStatus, Set<Integer> genreIds) {
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.authorName = authorName;
        this.artistName = artistName;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.genreIds = genreIds;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getArtistName() {
        return artistName;
    }

    public MangaResponse.MangaStatus getStatus() {
        return status;
    }

    public MangaResponse.ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public Set<Integer> getGenreIds() {
        return genreIds;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setStatus(MangaResponse.MangaStatus status) {
        this.status = status;
    }

    public void setApprovalStatus(MangaResponse.ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setGenreIds(Set<Integer> genreIds) {
        this.genreIds = genreIds;
    }
}
