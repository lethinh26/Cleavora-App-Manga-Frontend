package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.UploadResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApi {

    @Multipart
    @POST("v1/upload/image")
    Call<ApiResponse<UploadResponse>> uploadImage(
            @Part MultipartBody.Part file,
            @Part("folder") RequestBody folder
    );
}
