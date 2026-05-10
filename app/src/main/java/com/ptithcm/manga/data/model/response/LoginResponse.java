package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("tokenType")
    private String tokenType;

    @SerializedName("userId")
    private int userId;

    @SerializedName("email")
    private String email;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("role")
    private String role;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public String getAvatarUrl() { return avatarUrl; }
}
