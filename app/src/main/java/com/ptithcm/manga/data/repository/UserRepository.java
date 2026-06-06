package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.UserApi;
import com.ptithcm.manga.data.model.request.ProfileRequest;
import com.ptithcm.manga.data.model.request.ReadingHistoryRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.ReadingHistoryResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserApi userApi;

    public UserRepository(Context context) {
        userApi = ApiClient.getInstance(context).create(UserApi.class);
    }

    public interface UserCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public void getProfile(UserCallback<UserResponse> callback) {
        userApi.getProfile().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể lấy thông tin profile");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void updateProfile(String displayName, String email, String avatarUrl, UserCallback<UserResponse> callback){
        ProfileRequest request = new ProfileRequest(avatarUrl, displayName);
        userApi.updateProfile(request).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Cập nhật profile thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    // --- Reading History ---

    public void saveReadingProgress(int mangaId, int chapterId, int page, UserCallback<ReadingHistoryResponse> callback) {
        ReadingHistoryRequest request = new ReadingHistoryRequest(mangaId, chapterId, page);
        userApi.saveReadingProgress(request).enqueue(new Callback<ApiResponse<ReadingHistoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReadingHistoryResponse>> call, Response<ApiResponse<ReadingHistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi lưu vị trí đọc";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReadingHistoryResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getReadingProgress(int mangaId, UserCallback<ReadingHistoryResponse> callback) {
        userApi.getReadingProgress(mangaId).enqueue(new Callback<ApiResponse<ReadingHistoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReadingHistoryResponse>> call, Response<ApiResponse<ReadingHistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Không tìm thấy lịch sử đọc";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReadingHistoryResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getReadingHistory(UserCallback<List<ReadingHistoryResponse>> callback) {
        userApi.getReadingHistory().enqueue(new Callback<ApiResponse<List<ReadingHistoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReadingHistoryResponse>>> call, Response<ApiResponse<List<ReadingHistoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi lấy lịch sử đọc";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReadingHistoryResponse>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void deleteReadingHistory(int mangaId, UserCallback<Void> callback) {
        userApi.deleteReadingHistory(mangaId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi xóa lịch sử";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void deleteAllReadingHistory(UserCallback<Void> callback) {
        userApi.deleteAllReadingHistory().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lỗi xóa lịch sử";
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
