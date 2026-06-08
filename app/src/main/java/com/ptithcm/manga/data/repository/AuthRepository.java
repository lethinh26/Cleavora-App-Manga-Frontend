package com.ptithcm.manga.data.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.AuthApi;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.request.LoginRequest;
import com.ptithcm.manga.data.model.request.RegisterRequest;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;
    private final TokenManager tokenManager;

    public AuthRepository(Context context) {
        authApi = ApiClient.getInstance(context).create(AuthApi.class);
        tokenManager = TokenManager.getInstance(context);
    }


    public interface AuthCallback<T> {
        void onSuccess(String message, T data);
        void onError(String message);
    }

    public void register(String email, String password, String displayName, AuthCallback<Object> callback) {
        RegisterRequest request = new RegisterRequest(email, password, displayName);

        authApi.register(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> body = response.body();
                    if (body.isSuccess()) {
                        callback.onSuccess(body.getMessage(), body.getData());
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    callback.onError("Đăng ký thất bại. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void login(String email, String password, AuthCallback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(email, password);

        authApi.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> body = response.body();
                    if (body.isSuccess()) {
                        LoginResponse data = body.getData();

                        tokenManager.saveTokens(data.getAccessToken(), data.getRefreshToken());

                        tokenManager.saveUser(
                                data.getUserId(),
                                data.getEmail(),
                                data.getDisplayName(),
                                data.getRole(),
                                data.getAvatarUrl()
                        );

                        callback.onSuccess(body.getMessage(), data);
                    } else {
                        callback.onError(body.getMessage());
                    }
                } else {
                    // Try to parse error body for actual message
                    String errorMsg = "Email hoặc mật khẩu không đúng";
                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            // ErrorResponse format: {"timestamp":"...","status":403,"error":"...","message":"...","path":"..."}
                            Gson gson = new Gson();
                            ErrorResponse err = gson.fromJson(raw, ErrorResponse.class);
                            if (err != null && err.message != null && !err.message.isEmpty()) {
                                errorMsg = err.message;
                            }
                        }
                    } catch (Exception ignored) {}
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void logout() {
        tokenManager.clear();
    }

    /** Simple POJO for parsing ErrorResponse JSON from backend */
    private static class ErrorResponse {
        String message;
    }
}
