package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.request.LoginRequest;
import com.ptithcm.manga.data.model.request.RegisterRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface AuthApi {

    @POST("v1/auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest request);

    @POST("v1/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
}
