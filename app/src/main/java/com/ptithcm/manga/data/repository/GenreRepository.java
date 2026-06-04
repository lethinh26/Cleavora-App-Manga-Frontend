package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.AdminApi;
import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.MangaApi;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.GenreResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreRepository {
    private final MangaApi mangaApi;
    private final AdminApi adminApi;

    public GenreRepository(Context context) {
        mangaApi = ApiClient.getInstance(context).create(MangaApi.class);
        adminApi = ApiClient.getInstance(context).create(AdminApi.class);
    }

    // Public - get all genres
    public void getAllGenres(final RepositoryCallback<List<GenreResponse>> callback) {
        mangaApi.getGenres().enqueue(new Callback<ApiResponse<List<GenreResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GenreResponse>>> call,
                                   Response<ApiResponse<List<GenreResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi tải thể loại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GenreResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Admin - create genre
    public void createGenre(GenreResponse genre, final RepositoryCallback<GenreResponse> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("name", genre.getName());
        body.put("slug", genre.getSlug());
        adminApi.createGenre(body).enqueue(new Callback<ApiResponse<GenreResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GenreResponse>> call,
                                   Response<ApiResponse<GenreResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi thêm thể loại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GenreResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Admin - update genre
    public void updateGenre(int id, GenreResponse genre, final RepositoryCallback<GenreResponse> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("name", genre.getName());
        body.put("slug", genre.getSlug());
        adminApi.updateGenre(id, body).enqueue(new Callback<ApiResponse<GenreResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GenreResponse>> call,
                                   Response<ApiResponse<GenreResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi sửa thể loại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GenreResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Admin - delete genre
    public void deleteGenre(int id, final RepositoryCallback<Void> callback) {
        adminApi.deleteGenre(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(getErrorMsg(response, "Lỗi xóa thể loại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

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
