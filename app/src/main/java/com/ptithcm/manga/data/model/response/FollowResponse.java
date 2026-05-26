package com.ptithcm.manga.data.model.response;

public class FollowResponse {
    private boolean followed;
    private int followCount;

    public boolean isFollowed() { return followed; }
    public int getFollowCount() { return followCount; }
}
