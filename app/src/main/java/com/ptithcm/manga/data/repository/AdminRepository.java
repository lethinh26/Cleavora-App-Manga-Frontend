package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.AdminApi;
import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.DashboardStatsResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {
    private final AdminApi adminApi;

    public AdminRepository(Context context) {
        adminApi = ApiClient.getInstance(context).create(AdminApi.class);
    }

    // ============ DASHBOARD ============

    public void getDashboardStats(final RepositoryCallback<DashboardStatsResponse> callback) {
        adminApi.getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStatsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStatsResponse>> call, Response<ApiResponse<DashboardStatsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tải thống kê"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStatsResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // ============ USER MANAGEMENT ============

    public void getAllUsers(int page, int size, final RepositoryCallback<PageResponse<UserResponse>> callback) {
        adminApi.getAllUsers(page, size).enqueue(new Callback<ApiResponse<PageResponse<UserResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<UserResponse>>> call, Response<ApiResponse<PageResponse<UserResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tải danh sách người dùng"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<UserResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void toggleUserActive(int userId, final RepositoryCallback<UserResponse> callback) {
        adminApi.toggleUserActive(userId).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi cập nhật trạng thái"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void changeUserRole(int userId, String newRole, final RepositoryCallback<UserResponse> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("role", newRole);
        adminApi.changeUserRole(userId, body).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi thay đổi role"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // ============ MANGA APPROVAL ============

    public void getPendingMangas(int page, int size, final RepositoryCallback<PageResponse<MangaResponse>> callback) {
        adminApi.getPendingMangas(page, size).enqueue(new Callback<ApiResponse<PageResponse<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<MangaResponse>>> call, Response<ApiResponse<PageResponse<MangaResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tải danh sách truyện chờ duyệt"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<MangaResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void approveManga(int mangaId, final RepositoryCallback<MangaResponse> callback) {
        adminApi.approveManga(mangaId).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi duyệt truyện"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void rejectManga(int mangaId, String reason, final RepositoryCallback<MangaResponse> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("rejectReason", reason);
        adminApi.rejectManga(mangaId, body).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi từ chối truyện"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // ============ ADMIN MANGA CRUD ============

    public void getAllMangas(int page, int size, String sort, final RepositoryCallback<PageResponse<MangaResponse>> callback) {
        adminApi.getAllMangas(page, size, sort).enqueue(new Callback<ApiResponse<PageResponse<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<MangaResponse>>> call, Response<ApiResponse<PageResponse<MangaResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tải danh sách truyện"));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<PageResponse<MangaResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getMangaDetail(String id, final RepositoryCallback<MangaResponse> callback) {
        adminApi.getMangaDetail(id).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tải chi tiết truyện"));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void adminCreateManga(com.ptithcm.manga.data.model.request.MangaRequest request, final RepositoryCallback<MangaResponse> callback) {
        adminApi.createManga(request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tạo truyện"));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void adminUpdateManga(int id, com.ptithcm.manga.data.model.request.MangaRequest request, final RepositoryCallback<MangaResponse> callback) {
        adminApi.updateManga(id, request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi cập nhật truyện"));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteManga(int id, final RepositoryCallback<Void> callback) {
        adminApi.deleteManga(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    // Mấy API delete thường ko trả data nên cẩn thận
                    if (response.isSuccessful()) {
                         callback.onSuccess(null);
                    } else {
                        callback.onError(getErrorMsg((Response)response, "Lỗi xóa truyện"));
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // ============ HELPERS ============

    private <T> String getErrorMsg(Response<ApiResponse<T>> response, String fallback) {
        if (response.body() != null && response.body().getMessage() != null) {
            return response.body().getMessage();
        }
        return fallback;
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
