package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.MangaApi;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.FavoriteListResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaRepository {

    private final MangaApi mangaApi;

    public interface MangaCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public MangaRepository(Context context) {
        mangaApi = ApiClient.getInstance(context).create(MangaApi.class);
    }

    public void toggleLike(int mangaId, MangaCallback<LikeResponse> callback) {
        mangaApi.toggleLike(mangaId).enqueue(new Callback<ApiResponse<LikeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LikeResponse>> call, Response<ApiResponse<LikeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LikeResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể thực hiện thao tác like");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LikeResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getLikeStatus(int mangaId, MangaCallback<LikeStatusResponse> callback) {
        mangaApi.getLikeStatus(mangaId).enqueue(new Callback<ApiResponse<LikeStatusResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LikeStatusResponse>> call, Response<ApiResponse<LikeStatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LikeStatusResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể kiểm tra trạng thái like");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LikeStatusResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getFavorites(int page, int size, MangaCallback<FavoriteListResponse> callback) {
        mangaApi.getFavorites(page, size).enqueue(new Callback<ApiResponse<FavoriteListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FavoriteListResponse>> call, Response<ApiResponse<FavoriteListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<FavoriteListResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể tải danh sách yêu thích");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FavoriteListResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
