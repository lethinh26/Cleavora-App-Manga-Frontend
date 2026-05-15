package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.UserApi;
import com.ptithcm.manga.data.model.request.ChangePassRequest;
import com.ptithcm.manga.data.model.request.UpdateProfileRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.UserResponse;

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

    public void updateProfile(UpdateProfileRequest updateProfileRequest, UserCallback<UserResponse> callback) {
        userApi.updateProfile(updateProfileRequest).enqueue(new Callback<ApiResponse<UserResponse>>() {

            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    }else {
                        callback.onError(body.getMessage());
                    }
                }else {
                    callback.onError("Không thể cập nhật thông tin profile");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void changePassword(ChangePassRequest request, UserCallback<Void> callback) {
        userApi.changePassword(request).enqueue(new Callback<ApiResponse<Void>>() {

            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Không thể thay đổi mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }
}
