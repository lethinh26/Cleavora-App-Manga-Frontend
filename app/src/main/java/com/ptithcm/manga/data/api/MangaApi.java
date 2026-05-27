package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.request.MangaSubmitRequest;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.FavoriteListResponse;
import com.ptithcm.manga.data.model.response.FollowListResponse;
import com.ptithcm.manga.data.model.response.FollowResponse;
import com.ptithcm.manga.data.model.response.FollowStatusResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;

import retrofit2.Call;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MangaApi {
    @GET("v1/mangas")
    Call<ApiResponse<List<MangaResponse>>> getMangas();

    @GET("v1/genres/{slug}/mangas")
    Call<PageResponse<MangaResponse>> getMangasByGenre(@Path("slug") String slug);

    @GET("v1/genres")
    Call<ApiResponse<List<GenreResponse>>> getGenres();

    @GET("v1/mangas/{slug}")
    Call<ApiResponse<MangaResponse>> getMangaBySlug(@Path("slug") String slug);

    @GET("v1/mangas/search")
    Call<PageResponse<MangaResponse>> searchMangas(
            @Query("keyword") String keyword,

    @POST("v1/mangas/{id}/like")
    Call<ApiResponse<LikeResponse>> toggleLike(@Path("id") int mangaId);

    @GET("v1/mangas/{id}/like-status")
    Call<ApiResponse<LikeStatusResponse>> getLikeStatus(@Path("id") int mangaId);

    @GET("v1/me/favorites")
    Call<ApiResponse<FavoriteListResponse>> getFavorites(
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("v1/mangas/submit")
    Call<ApiResponse<MangaResponse>> submitManga(
            @Body MangaSubmitRequest request
    );

    @GET("v1/mangas/me")
    Call<ApiResponse<PageResponse<MangaResponse>>> getMyMangas(
            @Query("approval_status") String approvalStatus,
    @POST("v1/mangas/{id}/follow")
    Call<ApiResponse<FollowResponse>> toggleFollow(@Path("id") int mangaId);

    @GET("v1/mangas/{id}/follow-status")
    Call<ApiResponse<FollowStatusResponse>> getFollowStatus(@Path("id") int mangaId);

    @GET("v1/me/follows")
    Call<ApiResponse<FollowListResponse>> getFollows(
            @Query("page") int page,
            @Query("size") int size
    );
}
