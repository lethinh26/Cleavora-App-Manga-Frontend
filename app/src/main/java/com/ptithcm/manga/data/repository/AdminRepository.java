package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.AdminApi;
import com.ptithcm.manga.data.api.ApiClient;
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

    public interface AdminCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    // ============ DASHBOARD ============

    public void getDashboardStats(AdminCallback<DashboardStatsResponse> callback) {
        adminApi.getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStatsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStatsResponse>> call, Response<ApiResponse<DashboardStatsResponse>> response) {
                handleApiResponse(response, callback, "Không thể tải thống kê");
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStatsResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    // ============ USER MANAGEMENT ============

    public void getUsers(int page, int size, AdminCallback<PageResponse<UserResponse>> callback) {
        adminApi.getUsers(page, size).enqueue(new Callback<ApiResponse<PageResponse<UserResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<UserResponse>>> call, Response<ApiResponse<PageResponse<UserResponse>>> response) {
                handleApiResponse(response, callback, "Không thể tải danh sách người dùng");
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<UserResponse>>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void toggleUserActive(int userId, AdminCallback<UserResponse> callback) {
        adminApi.toggleUserActive(userId).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                handleApiResponse(response, callback, "Không thể thay đổi trạng thái tài khoản");
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void changeUserRole(int userId, String role, AdminCallback<UserResponse> callback) {
        ChangeRoleRequest request = new ChangeRoleRequest(role);
        adminApi.changeUserRole(userId, request).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                handleApiResponse(response, callback, "Không thể thay đổi role");
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    // ============ MANGA APPROVAL ============

    public void getPendingMangas(int page, int size, AdminCallback<PageResponse<MangaResponse>> callback) {
        adminApi.getPendingMangas(page, size).enqueue(new Callback<ApiResponse<PageResponse<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<MangaResponse>>> call, Response<ApiResponse<PageResponse<MangaResponse>>> response) {
                handleApiResponse(response, callback, "Không thể tải danh sách truyện chờ duyệt");
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<MangaResponse>>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void approveManga(int mangaId, AdminCallback<MangaResponse> callback) {
        adminApi.approveManga(mangaId).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không thể duyệt truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void rejectManga(int mangaId, String reason, AdminCallback<MangaResponse> callback) {
        RejectRequest request = new RejectRequest(reason);
        adminApi.rejectManga(mangaId, request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không thể từ chối truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    // ============ ADMIN MANGA CRUD ============

    public void getAdminMangas(int page, int size, String approvalStatus, AdminCallback<PageResponse<MangaResponse>> callback) {
        adminApi.getAdminMangas(page, size, approvalStatus).enqueue(new Callback<ApiResponse<PageResponse<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<MangaResponse>>> call, Response<ApiResponse<PageResponse<MangaResponse>>> response) {
                handleApiResponse(response, callback, "Không thể tải danh sách truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<MangaResponse>>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void getMangaById(int mangaId, AdminCallback<MangaResponse> callback) {
        adminApi.getMangaById(mangaId).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không thể tải thông tin truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void createManga(MangaSubmitRequest request, AdminCallback<MangaResponse> callback) {
        adminApi.createManga(request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không thể thêm truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void updateManga(int mangaId, MangaSubmitRequest request, AdminCallback<MangaResponse> callback) {
        adminApi.updateManga(mangaId, request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không thể cập nhật truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void deleteManga(int mangaId, AdminCallback<Object> callback) {
        adminApi.deleteManga(mangaId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                handleApiResponse(response, callback, "Không thể xóa truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    // ============ GENRE CRUD ============

    public void createGenre(String name, String slug, AdminCallback<GenreResponse> callback) {
        Map<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("slug", slug);
        adminApi.createGenre(request).enqueue(new Callback<ApiResponse<GenreResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GenreResponse>> call, Response<ApiResponse<GenreResponse>> response) {
                handleApiResponse(response, callback, "Không thể thêm thể loại");
            }

            @Override
            public void onFailure(Call<ApiResponse<GenreResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void updateGenre(int genreId, String name, String slug, AdminCallback<GenreResponse> callback) {
        Map<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("slug", slug);
        adminApi.updateGenre(genreId, request).enqueue(new Callback<ApiResponse<GenreResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GenreResponse>> call, Response<ApiResponse<GenreResponse>> response) {
                handleApiResponse(response, callback, "Không thể cập nhật thể loại");
            }

            @Override
            public void onFailure(Call<ApiResponse<GenreResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void deleteGenre(int genreId, AdminCallback<Void> callback) {
        adminApi.deleteGenre(genreId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                handleApiResponse(response, callback, "Không thể xóa thể loại");
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    // ============ CHAPTER CRUD ============

    public void createChapter(int mangaId, ChapterRequest request, AdminCallback<ChapterResponse> callback) {
        adminApi.createChapter(mangaId, request).enqueue(new Callback<ApiResponse<ChapterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChapterResponse>> call, Response<ApiResponse<ChapterResponse>> response) {
                handleApiResponse(response, callback, "Không thể thêm chapter");
            }

            @Override
            public void onFailure(Call<ApiResponse<ChapterResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void updateChapter(int chapterId, ChapterRequest request, AdminCallback<ChapterResponse> callback) {
        adminApi.updateChapter(chapterId, request).enqueue(new Callback<ApiResponse<ChapterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChapterResponse>> call, Response<ApiResponse<ChapterResponse>> response) {
                handleApiResponse(response, callback, "Không thể cập nhật chapter");
            }

            @Override
            public void onFailure(Call<ApiResponse<ChapterResponse>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void deleteChapter(int chapterId, AdminCallback<Object> callback) {
        adminApi.deleteChapter(chapterId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                handleApiResponse(response, callback, "Không thể xóa chapter");
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    // ============ HELPERS ============

    private <T> void handleApiResponse(Response<ApiResponse<T>> response, AdminCallback<T> callback, String defaultMessage) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> body = response.body();
            if (body.isSuccess()) {
                callback.onSuccess(body.getData());
            } else {
                callback.onError(body.getMessage() != null ? body.getMessage() : defaultMessage);
            }
        } else {
            callback.onError(errorWithCode(defaultMessage, response));
        }
    }

    private String networkError(Throwable t) {
        return "Lỗi mạng: " + (t != null && t.getMessage() != null ? t.getMessage() : "không xác định");
    }

    private String errorWithCode(String message, Response<?> response) {
        return message + " (Mã: " + response.code() + ")";
    }
}
