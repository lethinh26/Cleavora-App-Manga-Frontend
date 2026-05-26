package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LikeResponse {
    private boolean liked;
    @SerializedName("likeCount")
    private int likeCount;

    public boolean isLiked() { return liked; }
    public int getLikeCount() { return likeCount; }
}
