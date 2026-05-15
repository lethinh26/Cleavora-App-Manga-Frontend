package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("publicId")
    private String publicId;

    @SerializedName("width")
    private Integer width;

    @SerializedName("height")
    private Integer height;

    public String getImageUrl() { return imageUrl; }
    public String getPublicId() { return publicId; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
}
