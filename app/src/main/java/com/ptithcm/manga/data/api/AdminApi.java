package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.DashboardStatsResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApi {

    // Dashboard
    @GET("v1/admin/dashboard/stats")
    Call<ApiResponse<DashboardStatsResponse>> getDashboardStats();

    // User Management
    @GET("v1/admin/users")
    Call<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("v1/admin/users/{id}/toggle-active")
    Call<ApiResponse<UserResponse>> toggleUserActive(@Path("id") int userId);

    // Manga Approval
    @GET("v1/admin/mangas/pending")
    Call<ApiResponse<PageResponse<MangaResponse>>> getPendingMangas(
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("v1/admin/mangas/{id}/approve")
    Call<ApiResponse<MangaResponse>> approveManga(@Path("id") int mangaId);

    @PUT("v1/admin/mangas/{id}/reject")
    Call<ApiResponse<MangaResponse>> rejectManga(
            @Path("id") int mangaId,
            @Body Map<String, String> body
    );

    // SuperAdmin - Change Role
    @PUT("v1/superadmin/users/{id}/role")
    Call<ApiResponse<UserResponse>> changeUserRole(
            @Path("id") int userId,
            @Body Map<String, String> body
    );

    // Admin Manga CRUD
    @GET("v1/mangas")
    Call<ApiResponse<PageResponse<MangaResponse>>> getAllMangas(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("v1/mangas/{id}")
    Call<ApiResponse<MangaResponse>> getMangaDetail(@Path("id") String id);

    @POST("v1/admin/mangas")
    Call<ApiResponse<MangaResponse>> createManga(@Body com.ptithcm.manga.data.model.request.MangaRequest request);

    @PUT("v1/admin/mangas/{id}")
    Call<ApiResponse<MangaResponse>> updateManga(@Path("id") int id, @Body com.ptithcm.manga.data.model.request.MangaRequest request);

    @retrofit2.http.DELETE("v1/admin/mangas/{id}")
    Call<ApiResponse<Void>> deleteManga(@Path("id") int id);
}
