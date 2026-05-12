package com.ptithcm.manga.data.api;

import com.ptithcm.manga.BuildConfig;
import com.ptithcm.manga.data.local.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;
public class ApiClient {

    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static final int TIMEOUT = 30;

    private static Retrofit retrofit;

    public static Retrofit getInstance(android.content.Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        TokenManager tokenManager = TokenManager.getInstance(context);
                        String token = tokenManager.getAccessToken();
                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                        builder.header("Content-Type", "application/json");

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
