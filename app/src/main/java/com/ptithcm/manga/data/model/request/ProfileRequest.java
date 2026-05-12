package com.ptithcm.manga.data.model.request;

public class ProfileRequest {
    private String displayName;
    private String avatarUrl;

    public ProfileRequest( String avatarUrl, String displayName) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
