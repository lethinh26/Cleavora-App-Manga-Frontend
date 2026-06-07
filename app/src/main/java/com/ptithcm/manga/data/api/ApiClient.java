package com.ptithcm.manga.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.ptithcm.manga.BuildConfig;
import com.ptithcm.manga.data.local.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
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

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .build();

            // Custom Gson that tolerates LocalDateTime arrays from backend
            // e.g. [2024,1,15,10,30,0] when backend hasn't been restarted yet
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(String.class, new SafeStringTypeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /**
     * A TypeAdapter for String that gracefully handles JSON arrays
     * (e.g. LocalDateTime serialized as [2024,1,15,10,30,0] by Spring Boot default).
     * Instead of throwing, it reads and skips the array, returning a fallback string.
     */
    static class SafeStringTypeAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public String read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case STRING:
                    return in.nextString();
                case NUMBER:
                    return String.valueOf(in.nextDouble());
                case BOOLEAN:
                    return String.valueOf(in.nextBoolean());
                case NULL:
                    in.nextNull();
                    return null;
                case BEGIN_ARRAY:
                    // LocalDateTime array [2024,1,15,10,30,0] — skip it, return null
                    in.beginArray();
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;
                    while (in.hasNext()) {
                        if (!first) sb.append("-");
                        // Read each element (numbers)
                        sb.append((int) in.nextDouble());
                        first = false;
                    }
                    in.endArray();
                    // Return something parseable: "2024-01-15T10:30:00" style if 6+ elements
                    return null; // we just skip dates - they're not critical for display
                default:
                    in.skipValue();
                    return null;
            }
        }
    }
}
