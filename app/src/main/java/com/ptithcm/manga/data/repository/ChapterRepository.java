package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.ChapterApi;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.ChapterDetailResponse;
import com.ptithcm.manga.data.model.response.ChapterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterRepository {
    private final ChapterApi chapterApi;

    public ChapterRepository(Context context) {
        chapterApi = ApiClient.getInstance(context).create(ChapterApi.class);
    }

    public void getChaptersByMangaId(int mangaId, final RepositoryCallback<List<ChapterResponse>> callback) {
        chapterApi.getChaptersByMangaId(mangaId).enqueue(new Callback<ApiResponse<List<ChapterResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChapterResponse>>> call, Response<ApiResponse<List<ChapterResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to load chapters";
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChapterResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getChapterDetail(int chapterId, final RepositoryCallback<ChapterDetailResponse> callback) {
        chapterApi.getChapterDetail(chapterId).enqueue(new Callback<ApiResponse<ChapterDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChapterDetailResponse>> call, Response<ApiResponse<ChapterDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to load chapter detail";
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ChapterDetailResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void incrementViewCount(int chapterId, final RepositoryCallback<String> callback) {
        chapterApi.incrementViewCount(chapterId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to increment view count";
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
