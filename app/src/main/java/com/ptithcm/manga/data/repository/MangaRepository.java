package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.MangaApi;
import com.ptithcm.manga.data.model.request.MangaSubmitRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.FavoriteListResponse;
import com.ptithcm.manga.data.model.response.FollowListResponse;
import com.ptithcm.manga.data.model.response.FollowResponse;
import com.ptithcm.manga.data.model.response.FollowStatusResponse;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaRepository {
    private final MangaApi mangaApi;

    public MangaRepository(Context context) {
        mangaApi = ApiClient.getInstance(context).create(MangaApi.class);
    }

    public interface MangaCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public void getMangas(MangaCallback<List<MangaResponse>> callback) {
        getMangas(0, 20, "newest", null, callback);
    }

    public void getMangas(int page, int size, String sortBy, String status, MangaCallback<List<MangaResponse>> callback) {
        mangaApi.getMangas(page, size, sortBy, status).enqueue(new Callback<ApiResponse<List<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MangaResponse>>> call, Response<ApiResponse<List<MangaResponse>>> response) {
                handleApiResponse(response, callback, "Lấy danh sách truyện thất bại");
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MangaResponse>>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getGenres(MangaCallback<List<GenreResponse>> callback) {
        mangaApi.getGenres().enqueue(new Callback<ApiResponse<List<GenreResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GenreResponse>>> call, Response<ApiResponse<List<GenreResponse>>> response) {
                handleApiResponse(response, callback, "Lỗi lấy danh sách thể loại");
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GenreResponse>>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getMangasByGenre(String slug, MangaCallback<List<MangaResponse>> callback) {
        mangaApi.getMangasByGenre(slug, 0, 20).enqueue(new Callback<PageResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MangaResponse>> call, Response<PageResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getContent());
                } else {
                    callback.onError(errorWithCode("Không tìm thấy truyện thuộc thể loại này", response));
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getMangaBySlug(String slug, MangaCallback<MangaResponse> callback) {
        mangaApi.getMangaBySlug(slug).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không tìm thấy truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getMangaById(int mangaId, MangaCallback<MangaResponse> callback) {
        mangaApi.getMangaById(mangaId).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không tìm thấy truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void searchManga(String keyword, int page, int size, MangaCallback<PageResponse<MangaResponse>> callback) {
        mangaApi.searchMangas(keyword, page, size).enqueue(new Callback<PageResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<MangaResponse>> call, Response<PageResponse<MangaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorWithCode("Không tìm thấy truyện phù hợp", response));
                }
            }

            @Override
            public void onFailure(Call<PageResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void toggleLike(int mangaId, MangaCallback<LikeResponse> callback) {
        mangaApi.toggleLike(mangaId).enqueue(new Callback<ApiResponse<LikeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LikeResponse>> call, Response<ApiResponse<LikeResponse>> response) {
                handleApiResponse(response, callback, "Không thể thực hiện thao tác like");
            }

            @Override
            public void onFailure(Call<ApiResponse<LikeResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getLikeStatus(int mangaId, MangaCallback<LikeStatusResponse> callback) {
        mangaApi.getLikeStatus(mangaId).enqueue(new Callback<ApiResponse<LikeStatusResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LikeStatusResponse>> call, Response<ApiResponse<LikeStatusResponse>> response) {
                handleApiResponse(response, callback, "Không thể kiểm tra trạng thái like");
            }

            @Override
            public void onFailure(Call<ApiResponse<LikeStatusResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getFavorites(int page, int size, MangaCallback<FavoriteListResponse> callback) {
        mangaApi.getFavorites(page, size).enqueue(new Callback<ApiResponse<FavoriteListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FavoriteListResponse>> call, Response<ApiResponse<FavoriteListResponse>> response) {
                handleApiResponse(response, callback, "Không thể tải danh sách yêu thích");
            }

            @Override
            public void onFailure(Call<ApiResponse<FavoriteListResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void toggleFollow(int mangaId, MangaCallback<FollowResponse> callback) {
        mangaApi.toggleFollow(mangaId).enqueue(new Callback<ApiResponse<FollowResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowResponse>> call, Response<ApiResponse<FollowResponse>> response) {
                handleApiResponse(response, callback, "Không thể thực hiện thao tác theo dõi");
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getFollowStatus(int mangaId, MangaCallback<FollowStatusResponse> callback) {
        mangaApi.getFollowStatus(mangaId).enqueue(new Callback<ApiResponse<FollowStatusResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowStatusResponse>> call, Response<ApiResponse<FollowStatusResponse>> response) {
                handleApiResponse(response, callback, "Không thể kiểm tra trạng thái theo dõi");
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowStatusResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getFollows(int page, int size, MangaCallback<FollowListResponse> callback) {
        mangaApi.getFollows(page, size).enqueue(new Callback<ApiResponse<FollowListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowListResponse>> call, Response<ApiResponse<FollowListResponse>> response) {
                handleApiResponse(response, callback, "Không thể tải danh sách theo dõi");
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowListResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void submitManga(MangaSubmitRequest request, MangaCallback<MangaResponse> callback) {
        mangaApi.submitManga(request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Submit truyện thất bại");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void getMyMangas(String approvalStatus, int page, int size, MangaCallback<PageResponse<MangaResponse>> callback) {
        mangaApi.getMyMangas(approvalStatus, page, size).enqueue(new Callback<ApiResponse<PageResponse<MangaResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<MangaResponse>>> call, Response<ApiResponse<PageResponse<MangaResponse>>> response) {
                handleApiResponse(response, callback, "Không thể tải truyện của tôi");
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<MangaResponse>>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void deleteMyManga(int mangaId, MangaCallback<Object> callback) {
        mangaApi.deleteMyManga(mangaId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                handleApiResponse(response, callback, "Không thể xoá truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    public void updateMyManga(int mangaId, MangaSubmitRequest request, MangaCallback<MangaResponse> callback) {
        mangaApi.updateMyManga(mangaId, request).enqueue(new Callback<ApiResponse<MangaResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MangaResponse>> call, Response<ApiResponse<MangaResponse>> response) {
                handleApiResponse(response, callback, "Không thể cập nhật truyện");
            }

            @Override
            public void onFailure(Call<ApiResponse<MangaResponse>> call, Throwable throwable) {
                callback.onError(networkError(throwable));
            }
        });
    }

    private <T> void handleApiResponse(Response<ApiResponse<T>> response, MangaCallback<T> callback, String defaultMessage) {
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

    private String networkError(Throwable throwable) {
        return "Lỗi mạng: " + (throwable != null && throwable.getMessage() != null ? throwable.getMessage() : "không xác định");
    }

    private String errorWithCode(String message, Response<?> response) {
        return message + " (Mã: " + response.code() + ")";
    }
}
