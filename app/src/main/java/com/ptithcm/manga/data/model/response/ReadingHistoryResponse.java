package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;

public class ReadingHistoryResponse {

    @SerializedName("mangaId")
    private int mangaId;

    @SerializedName("chapterId")
    private Integer chapterId;

    @SerializedName("chapterNumber")
    private Double chapterNumber;

    @SerializedName("lastPage")
    private int lastPage;

    @SerializedName("lastReadAt")
    private String lastReadAt;

    // FE-only fields (filled after manga lookup, not from API)
    private String mangaTitle;
    private String mangaCoverUrl;
    private String mangaSlug;
    private String chapterTitle;

    public int getMangaId() { return mangaId; }
    public Integer getChapterId() { return chapterId; }
    public Double getChapterNumber() { return chapterNumber; }
    public int getLastPage() { return lastPage; }
    public String getLastReadAt() { return lastReadAt; }

    public String getMangaTitle() { return mangaTitle; }
    public void setMangaTitle(String mangaTitle) { this.mangaTitle = mangaTitle; }

    public String getMangaCoverUrl() { return mangaCoverUrl; }
    public void setMangaCoverUrl(String mangaCoverUrl) { this.mangaCoverUrl = mangaCoverUrl; }

    public String getMangaSlug() { return mangaSlug; }
    public void setMangaSlug(String mangaSlug) { this.mangaSlug = mangaSlug; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
}
