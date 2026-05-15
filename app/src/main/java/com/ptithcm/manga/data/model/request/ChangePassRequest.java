package com.ptithcm.manga.data.model.request;

public class ChangePassRequest {
    private String oldPassword;
    private String newPassword;

    public ChangePassRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
}
