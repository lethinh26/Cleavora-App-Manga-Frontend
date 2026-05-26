package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.FavoriteListResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MangaApi {

    @POST("v1/mangas/{id}/like")
    Call<ApiResponse<LikeResponse>> toggleLike(@Path("id") int mangaId);

    @GET("v1/mangas/{id}/like-status")
    Call<ApiResponse<LikeStatusResponse>> getLikeStatus(@Path("id") int mangaId);

    @GET("v1/me/favorites")
    Call<ApiResponse<FavoriteListResponse>> getFavorites(
            @Query("page") int page,
            @Query("size") int size
    );
}
