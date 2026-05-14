package com.ptithcm.manga.data.model.request;

public class UpdateProfileRequest {
    private String avatarUrl;
    private String displayName;

    public UpdateProfileRequest(String avatarUrl, String displayName) {
        this.avatarUrl = avatarUrl;
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }
}
