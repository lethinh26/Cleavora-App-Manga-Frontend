package com.ptithcm.manga.data.model.response;

public class DashboardStatsResponse {
    private long totalUsers;
    private long totalMangas;
    private long totalChapters;
    private long totalPendingMangas;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalMangas() { return totalMangas; }
    public void setTotalMangas(long totalMangas) { this.totalMangas = totalMangas; }
    public long getTotalChapters() { return totalChapters; }
    public void setTotalChapters(long totalChapters) { this.totalChapters = totalChapters; }
    public long getTotalPendingMangas() { return totalPendingMangas; }
    public void setTotalPendingMangas(long totalPendingMangas) { this.totalPendingMangas = totalPendingMangas; }
}
