package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.UploadResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public interface CloudinaryApi {
    @Multipart
    @POST("v1_1/dbsu61unm/image/upload")
    Call<UploadResponse> uploadImage(
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody uploadPreset
    );
}
