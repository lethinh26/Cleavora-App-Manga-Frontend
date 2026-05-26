package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FavoriteListResponse {
    private List<MangaResponse> content;
    private int page;
    private int size;
    @SerializedName("totalElements")
    private long totalElements;
    @SerializedName("totalPages")
    private int totalPages;
    private boolean last;

    public List<MangaResponse> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
}
