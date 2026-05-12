package com.ptithcm.manga.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ptithcm.manga.data.api.CloudinaryApi;
import com.ptithcm.manga.data.model.response.UploadResponse;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudRepository {

    public void uploadImageToCloudinary(Context context, Uri imageUri, Callback<UploadResponse> callback) {
        try {
            File file = new File(context.getCacheDir(), "upload.jpg");
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                Files.copy(inputStream, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                inputStream.close();
            }

            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RequestBody preset = RequestBody.create("foodara", MultipartBody.FORM);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.cloudinary.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CloudinaryApi api = retrofit.create(CloudinaryApi.class);

            api.uploadImage(body, preset).enqueue(callback);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
