package com.ptithcm.manga.data.model.response;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int totalElements;
    private int totalPages;
    private int number;
    private int size;
    private boolean last;
    private boolean first;
    private boolean empty;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isEmpty() {
        return empty;
    }
}
