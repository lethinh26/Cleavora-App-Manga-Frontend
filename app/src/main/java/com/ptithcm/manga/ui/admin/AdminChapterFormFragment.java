package com.ptithcm.manga.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.api.CloudinaryApi;
import com.ptithcm.manga.data.model.request.ChapterRequest;
import com.ptithcm.manga.data.model.response.ChapterResponse;
import com.ptithcm.manga.data.repository.ChapterRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminChapterFormFragment extends Fragment {

    private ChapterRepository chapterRepository;
    private int mangaId;
    
    private EditText etChapterIndex, etChapterTitle;
    private Button btnSelectImages;
    private TextView tvImageCount;
    private MaterialButton btnSave;
    
    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<String> uploadedImageUrls = new ArrayList<>();
    private int uploadSuccessCount = 0;

    private final ActivityResultLauncher<String> pickMultipleImages = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    selectedImageUris.clear();
                    selectedImageUris.addAll(uris);
                    tvImageCount.setText("Đã chọn " + uris.size() + " ảnh");
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_chapter_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        chapterRepository = new ChapterRepository(requireContext());
        
        if (getArguments() != null) {
            mangaId = getArguments().getInt("mangaId", -1);
        }

        if (mangaId == -1) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy Manga ID", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        etChapterIndex = view.findViewById(R.id.et_chapter_index);
        etChapterTitle = view.findViewById(R.id.et_chapter_title);
        btnSelectImages = view.findViewById(R.id.btn_select_images);
        tvImageCount = view.findViewById(R.id.tv_image_count);
        btnSave = view.findViewById(R.id.btn_save);

        btnSelectImages.setOnClickListener(v -> pickMultipleImages.launch("image/*"));
        btnSave.setOnClickListener(v -> saveChapter());
    }

    private void saveChapter() {
        if (etChapterIndex.getText().toString().isEmpty() || selectedImageUris.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số chương và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Đang upload ảnh...");
        
        uploadedImageUrls.clear();
        uploadSuccessCount = 0;
        
        for (Uri uri : selectedImageUris) {
            CloudinaryApi.uploadImage(requireContext(), uri, new CloudinaryApi.UploadCallback() {
                @Override
                public void onSuccess(String url) {
                    uploadedImageUrls.add(url);
                    uploadSuccessCount++;
                    if (uploadSuccessCount == selectedImageUris.size()) {
                        submitData();
                    }
                }

                @Override
                public void onError(String message) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Lưu Chapter");
                    Toast.makeText(getContext(), "Lỗi upload ảnh: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void submitData() {
        btnSave.setText("Đang lưu...");
        
        ChapterRequest req = new ChapterRequest();
        req.setChapterIndex(Float.parseFloat(etChapterIndex.getText().toString()));
        req.setTitle(etChapterTitle.getText().toString());
        req.setImageUrls(uploadedImageUrls);

        chapterRepository.createChapter(mangaId, req, new ChapterRepository.RepositoryCallback<ChapterResponse>() {
            @Override
            public void onSuccess(ChapterResponse result) {
                Toast.makeText(getContext(), "Thêm chapter thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }

            @Override
            public void onError(String message) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu Chapter");
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
