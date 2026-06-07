package com.ptithcm.manga.data.model.response;

import java.time.LocalDateTime;
import java.util.Set;

public class MangaResponse {
    private Integer id;

    private String title;

    private String slug;

    private String description;

    private String coverImageUrl;

    public MangaResponse(Integer id, String title, String slug, String description, String coverImageUrl, String authorName, String artistName, MangaStatus status, ApprovalStatus approvalStatus, Integer viewCount, Integer likeCount, Integer followCount, String createdAt, String updatedAt, Set<String> genres, Integer totalChapters, Integer submittedById) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.authorName = authorName;
        this.artistName = artistName;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.followCount = followCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.genres = genres;
        this.totalChapters = totalChapters;
        this.submittedById = submittedById;
    }

    private String authorName;

    private String artistName;

    private MangaStatus status;

    private ApprovalStatus approvalStatus;

    private Integer viewCount;

    private Integer likeCount;

    private Integer followCount;

    private String createdAt;

    private String updatedAt;

    private Set<String> genres;

    private Integer totalChapters;

    private Integer submittedById;

    public enum MangaStatus{
        ONGOING, COMPLETED, HIATUS
    }

    public enum ApprovalStatus{
        PENDING, APPROVED, REJECTED
    }

    public MangaResponse() {
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setStatus(MangaStatus status) {
        this.status = status;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public void setTotalChapters(Integer totalChapters) {
        this.totalChapters = totalChapters;
    }

    public Integer getId() {
        return id;
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

    public MangaStatus getStatus() {
        return status;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public Integer getTotalChapters() {
        return totalChapters;
    }

    public Integer getSubmittedById() {
        return submittedById;
    }

    public void setSubmittedById(Integer submittedById) {
        this.submittedById = submittedById;
    }
}
