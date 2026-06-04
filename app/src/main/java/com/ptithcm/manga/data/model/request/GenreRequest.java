package com.ptithcm.manga.data.model.request;

public class GenreRequest {
    private String name;
    private String slug;

    public GenreRequest() {}

    public GenreRequest(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}
