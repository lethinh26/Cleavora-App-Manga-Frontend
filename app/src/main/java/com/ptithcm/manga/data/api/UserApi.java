package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.request.ProfileRequest;
import com.ptithcm.manga.data.model.request.ReadingHistoryRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.ReadingHistoryResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    @GET("v1/me/profile")
    Call<ApiResponse<UserResponse>> getProfile();

    @PUT("v1/me/profile")
    Call<ApiResponse<UserResponse>> updateProfile(@Body ProfileRequest request);

    // --- Reading History ---

    @PUT("v1/me/history")
    Call<ApiResponse<ReadingHistoryResponse>> saveReadingProgress(@Body ReadingHistoryRequest request);

    @GET("v1/me/history/{mangaId}")
    Call<ApiResponse<ReadingHistoryResponse>> getReadingProgress(@Path("mangaId") int mangaId);

    @GET("v1/me/history")
    Call<ApiResponse<List<ReadingHistoryResponse>>> getReadingHistory();

    @DELETE("v1/me/history/{mangaId}")
    Call<ApiResponse<Void>> deleteReadingHistory(@Path("mangaId") int mangaId);

    @DELETE("v1/me/history")
    Call<ApiResponse<Void>> deleteAllReadingHistory();
}
