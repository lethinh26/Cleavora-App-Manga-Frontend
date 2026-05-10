package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    private int id;

    private String email;

    private String displayName;

    private String avatarUrl;

    private String role;

    private String createdAt;

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }
    public String getCreatedAt() { return createdAt; }
}
