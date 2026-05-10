package com.ptithcm.manga.data.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    private String email;

    private String password;

    private String displayName;

    public RegisterRequest(String email, String password, String displayName) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDisplayName() { return displayName; }
}
