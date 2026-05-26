package com.ptithcm.manga.data.model.response;

public class GenreResponse {
    private Integer id;
    private String name;
    private String slug;

    public GenreResponse(Integer id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GenreResponse() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }
}
