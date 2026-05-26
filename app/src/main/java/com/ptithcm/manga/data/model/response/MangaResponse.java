package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MangaResponse {
    private int id;
    private String title;
    private String slug;
    private String description;
    @SerializedName("coverImageUrl")
    private String coverImageUrl;
    @SerializedName("authorName")
    private String authorName;
    @SerializedName("artistName")
    private String artistName;
    private String status;
    @SerializedName("viewCount")
    private int viewCount;
    @SerializedName("likeCount")
    private int likeCount;
    @SerializedName("followCount")
    private int followCount;
    @SerializedName("followCount")
    private int followCount;
    private List<String> genres;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getAuthorName() { return authorName; }
    public String getArtistName() { return artistName; }
    public String getStatus() { return status; }
    public int getViewCount() { return viewCount; }
    public int getLikeCount() { return likeCount; }
    public int getFollowCount() { return followCount; }
    public List<String> getGenres() { return genres; }
}
