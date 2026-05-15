package com.ptithcm.manga.data.repository;

import android.content.Context;
import android.net.Uri;

import com.ptithcm.manga.data.api.ApiClient;
import com.ptithcm.manga.data.api.UploadApi;
import com.ptithcm.manga.data.model.response.ApiResponse;
import com.ptithcm.manga.data.model.response.UploadResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UploadRepository {

    private final UploadApi uploadApi;
    private final Context context;

    public UploadRepository(Context context) {
        this.context = context.getApplicationContext();
        this.uploadApi = ApiClient.getInstance(context).create(UploadApi.class);
    }


    public Call<ApiResponse<UploadResponse>> uploadImage(Uri imageUri, String folder) {
        File file = uriToFile(imageUri);

        String mimeType = context.getContentResolver().getType(imageUri);
        if (mimeType == null) mimeType = "image/jpeg";

        RequestBody requestFile = RequestBody.create(
                MediaType.parse(mimeType), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file", file.getName(), requestFile);
        RequestBody folderPart = RequestBody.create(
                MediaType.parse("text/plain"), folder);

        return uploadApi.uploadImage(filePart, folderPart);
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("upload_", ".jpg", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Không thể đọc file ảnh", e);
        }
    }
}
