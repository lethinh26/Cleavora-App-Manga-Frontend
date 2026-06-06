package com.ptithcm.manga.data.model.request;

public class ReadingHistoryRequest {

    private int mangaId;
    private int chapterId;
    private int page; // ⚠️ Backend field là "page", KHÔNG phải "lastPage"

    public ReadingHistoryRequest(int mangaId, int chapterId, int page) {
        this.mangaId = mangaId;
        this.chapterId = chapterId;
        this.page = page;
    }

    public int getMangaId() { return mangaId; }
    public int getChapterId() { return chapterId; }
    public int getPage() { return page; }
}
