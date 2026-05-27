package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.MangaApi;
import com.ptithcm.manga.data.model.request.MangaSubmitRequest;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;

import java.util.List;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.FavoriteListResponse;
import com.ptithcm.manga.data.model.response.FollowListResponse;
import com.ptithcm.manga.data.model.response.FollowResponse;
import com.ptithcm.manga.data.model.response.FollowStatusResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaRepository {
    private MangaApi mangaApi;

    public MangaRepository(Context context) {
        mangaApi = ApiClient.getInstance(context).create(MangaApi.class);
    }

    public interface MangaCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public void getMangas(MangaCallback<List<MangaResponse>> callback) {
        mangaApi.getMangas().enqueue(new Callback<ApiResponse<List<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MangaResponse>>> call, Response<ApiResponse<List<MangaResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<MangaResponse>> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage() != null ? body.getMessage() : "Lấy danh sách truyện thất bại");
                    }
                } else {
                    callback.onError("Không thể kết nối server (Mã: " + response.code() + ")");

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
            public void onFailure(Call<ApiResponse<List<MangaResponse>>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void getGenres(MangaCallback<List<GenreResponse>> callback) {
        mangaApi.getGenres().enqueue(new Callback<ApiResponse<List<GenreResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GenreResponse>>> call, Response<ApiResponse<List<GenreResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<GenreResponse>> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage() != null ? body.getMessage() : "Lỗi lấy danh sách thể loại");
                    }
                } else {
                    callback.onError("Không thể lấy thể loại (Mã: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GenreResponse>>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void getMangasByGenre(String slug, MangaCallback<List<MangaResponse>> callback) {
        mangaApi.getMangasByGenre(slug).enqueue(new Callback<PageResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MangaResponse>> call, Response<PageResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getContent());
                } else {
                    callback.onError("Không tìm thấy truyện thuộc thể loại này");
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
            public void onFailure(Call<PageResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void getMangaBySlug(String slug, MangaCallback<MangaResponse> callback) {
        mangaApi.getMangaBySlug(slug).enqueue(new Callback<ApiResponse<MangaResponse>>() {

            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Không tìm thấy truyện");
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
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage() + "");
            }
        });
    }

    public void searchManga(String keyword, int page, int size, MangaCallback<PageResponse<MangaResponse>> callback){
        mangaApi.searchMangas(keyword, page, size).enqueue(new Callback<PageResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MangaResponse>> call, Response<PageResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tìm thấy truyện phù hợp");
            public void onFailure(Call<ApiResponse<FavoriteListResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void toggleFollow(int mangaId, MangaCallback<FollowResponse> callback) {
        mangaApi.toggleFollow(mangaId).enqueue(new Callback<ApiResponse<FollowResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowResponse>> call, Response<ApiResponse<FollowResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<FollowResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể thực hiện thao tác theo dõi");
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void submitManga(MangaSubmitRequest request, MangaCallback<MangaResponse> callback) {
        mangaApi.submitManga(request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<MangaResponse> body = response.body();
            public void onFailure(Call<ApiResponse<FollowResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getFollowStatus(int mangaId, MangaCallback<FollowStatusResponse> callback) {
        mangaApi.getFollowStatus(mangaId).enqueue(new Callback<ApiResponse<FollowStatusResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowStatusResponse>> call, Response<ApiResponse<FollowStatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<FollowStatusResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể kiểm tra trạng thái theo dõi");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void getMyMangas(String approvalStatus, int page, int size, MangaCallback<PageResponse<MangaResponse>> callback) {
        mangaApi.getMyMangas(approvalStatus, page, size).enqueue(new Callback<ApiResponse<PageResponse<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<MangaResponse>>> call, Response<ApiResponse<PageResponse<MangaResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PageResponse<MangaResponse>> body = response.body();
            public void onFailure(Call<ApiResponse<FollowStatusResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getFollows(int page, int size, MangaCallback<FollowListResponse> callback) {
        mangaApi.getFollows(page, size).enqueue(new Callback<ApiResponse<FollowListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowListResponse>> call, Response<ApiResponse<FollowListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<FollowListResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể tải danh sách theo dõi");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowListResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
