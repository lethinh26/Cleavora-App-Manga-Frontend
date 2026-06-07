package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.request.ChangeRoleRequest;
import com.ptithcm.manga.data.model.request.ChapterRequest;
import com.ptithcm.manga.data.model.request.MangaSubmitRequest;
import com.ptithcm.manga.data.model.request.RejectRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.ChapterResponse;
import com.ptithcm.manga.data.model.response.DashboardStatsResponse;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApi {

    // ============ DASHBOARD (#45) ============

    @GET("v1/admin/dashboard/stats")
    Call<ApiResponse<DashboardStatsResponse>> getDashboardStats();

    // ============ USER MANAGEMENT (#43, #44, #46) ============

    @GET("v1/admin/users")
    Call<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("v1/admin/users/{id}/toggle-active")
    Call<ApiResponse<UserResponse>> toggleUserActive(@Path("id") int userId);

    @PUT("v1/superadmin/users/{id}/role")
    Call<ApiResponse<UserResponse>> changeUserRole(
            @Path("id") int userId,
            @Body ChangeRoleRequest request
    );

    // ============ MANGA APPROVAL (#34, #35, #36) ============

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
            @Body RejectRequest request
    );

    // ============ ADMIN MANGA MANAGEMENT ============

    @GET("v1/admin/mangas")
    Call<ApiResponse<PageResponse<MangaResponse>>> getAdminMangas(
            @Query("page") int page,
            @Query("size") int size,
            @Query("approval_status") String approvalStatus
    );

    @GET("v1/admin/mangas/{id}")
    Call<ApiResponse<MangaResponse>> getMangaById(@Path("id") int mangaId);

    @POST("v1/admin/mangas")
    Call<ApiResponse<MangaResponse>> createManga(@Body MangaSubmitRequest request);

    // ============ BAN/UNBAN ============

    @PUT("v1/admin/mangas/{id}/ban")
    Call<ApiResponse<MangaResponse>> banManga(
            @Path("id") int mangaId,
            @Body RejectRequest request
    );

    @PUT("v1/admin/mangas/{id}/unban")
    Call<ApiResponse<MangaResponse>> unbanManga(@Path("id") int mangaId);

    // ============ GENRE CRUD (#40, #41, #42) ============

    @POST("v1/admin/genres")
    Call<ApiResponse<GenreResponse>> createGenre(@Body Map<String, String> request);

    @PUT("v1/admin/genres/{id}")
    Call<ApiResponse<GenreResponse>> updateGenre(
            @Path("id") int genreId,
            @Body Map<String, String> request
    );

    @DELETE("v1/admin/genres/{id}")
    Call<ApiResponse<Void>> deleteGenre(@Path("id") int genreId);
}
