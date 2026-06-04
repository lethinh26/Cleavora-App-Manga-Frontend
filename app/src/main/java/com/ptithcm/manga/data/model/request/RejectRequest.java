package com.ptithcm.manga.data.model.request;

public class RejectRequest {
    private String rejectReason;

    public RejectRequest() {}

    public RejectRequest(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
