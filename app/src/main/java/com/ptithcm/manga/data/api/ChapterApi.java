package com.ptithcm.manga.data.api;

import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.ChapterDetailResponse;
import com.ptithcm.manga.data.model.response.ChapterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChapterApi {
    @GET("v1/mangas/{mangaId}/chapters")
    Call<ApiResponse<List<ChapterResponse>>> getChaptersByMangaId(@Path("mangaId") int mangaId);

    @GET("v1/chapters/{chapterId}")
    Call<ApiResponse<ChapterDetailResponse>> getChapterDetail(@Path("chapterId") int chapterId);

    @POST("v1/chapters/{chapterId}/view")
    Call<ApiResponse<String>> incrementViewCount(@Path("chapterId") int chapterId);
}
