package com.ptithcm.manga.data.model.request;

public class ChangeRoleRequest {
    private String role; // "USER", "ADMIN"

    public ChangeRoleRequest() {}

    public ChangeRoleRequest(String role) {
        this.role = role;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
