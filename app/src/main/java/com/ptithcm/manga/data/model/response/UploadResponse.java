package com.ptithcm.manga.data.model.response;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName(value = "publicId", alternate = {"public_id"})
    public String publicId;
    
    @SerializedName(value = "imageUrl", alternate = {"secure_url"})
    public String imageUrl;
}