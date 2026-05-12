package com.ptithcm.manga.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "manga_auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_AVATAR_URL = "avatar_url";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void saveUser(int userId, String email, String displayName, String role, String avatarUrl) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_DISPLAY_NAME, displayName)
                .putString(KEY_ROLE, role)
                .putString(KEY_AVATAR_URL, avatarUrl)
                .apply();
    }

    public int getUserId() { return prefs.getInt(KEY_USER_ID, -1); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getDisplayName() { return prefs.getString(KEY_DISPLAY_NAME, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }
    public String getAvatarUrl() { return prefs.getString(KEY_AVATAR_URL, null); }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
