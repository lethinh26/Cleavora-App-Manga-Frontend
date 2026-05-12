package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.request.ProfileRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {

    @GET("v1/me/profile")
    Call<ApiResponse<UserResponse>> getProfile();

    @PUT("v1/me/profile")
    Call<ApiResponse<UserResponse>> updateProfile(@Body ProfileRequest request);
}
