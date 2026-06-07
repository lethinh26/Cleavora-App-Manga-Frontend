package com.ptithcm.manga.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.genre.MultiSelectGenreAdapter;
import com.ptithcm.manga.data.model.request.MangaSubmitRequest;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.UploadResponse;
import com.ptithcm.manga.data.repository.AdminRepository;
import com.ptithcm.manga.data.repository.MangaRepository;
import com.ptithcm.manga.data.repository.UploadRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMangaFormFragment extends Fragment {

    private AdminRepository adminRepository;
    private MangaRepository mangaRepository;
    private UploadRepository uploadRepository;

    private TextView tvFormTitle;
    private com.google.android.material.textfield.TextInputEditText etTitle, etAuthor, etArtist, etDescription;
    private Spinner spinnerStatus;
    private ImageView ivCover;
    private RecyclerView rvGenres;
    private MultiSelectGenreAdapter genreAdapter;
    private com.google.android.material.button.MaterialButton btnSave;

    private String coverUrl;
    private int mangaId = -1; // -1 = create mode
    private final Set<Integer> selectedGenreIds = new HashSet<>();
    // Dùng để handle race condition giữa 2 API loadGenres và loadMangaData
    private Set<String> pendingGenreNames = null;   // Tên genre cần pre-select
    private List<GenreResponse> loadedGenres = null; // Danh sách genre đã load

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        uploadCoverImage(selectedImage);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manga_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminRepository = new AdminRepository(requireContext());
        mangaRepository = new MangaRepository(requireContext());
        uploadRepository = new UploadRepository(requireContext());

        if (getArguments() != null) {
            mangaId = getArguments().getInt("mangaId", -1);
        }

        tvFormTitle = view.findViewById(R.id.tv_form_title);
        etTitle = view.findViewById(R.id.et_title);
        etAuthor = view.findViewById(R.id.et_author);
        etArtist = view.findViewById(R.id.et_artist);
        etDescription = view.findViewById(R.id.et_description);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        ivCover = view.findViewById(R.id.iv_cover);
        rvGenres = view.findViewById(R.id.rv_genres);
        btnSave = view.findViewById(R.id.btn_save);

        // Status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"ONGOING", "COMPLETED", "HIATUS"});
        spinnerStatus.setAdapter(statusAdapter);

        // Genre multi-select
        genreAdapter = new MultiSelectGenreAdapter();
        rvGenres.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        rvGenres.setAdapter(genreAdapter);

        // Cover picker
        View coverContainer = view.findViewById(R.id.cover_container);
        if (coverContainer != null) {
            coverContainer.setOnClickListener(v -> pickImage());
        }

        // Load genres
        loadGenres();

        boolean isEditMode = mangaId > 0;
        if (isEditMode) {
            tvFormTitle.setText("Sửa truyện");
            loadMangaData(mangaId);
        } else {
            tvFormTitle.setText("Thêm truyện");
        }

        btnSave.setOnClickListener(v -> saveManga());
    }

    private void loadGenres() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    loadedGenres = data;
                    genreAdapter.setGenres(data);
                    // Nếu manga data đã load xong trước, map ngay
                    applyPendingGenreSelection();
                });
            }

            @Override
            public void onError(String message) {
                // silently fail, genres are optional for form rendering
            }
        });
    }

    /**
     * Map tên thể loại (Set<String>) sang ID và pre-select trong adapter.
     * Chỉ được gọi khi cả 2 loadedGenres và pendingGenreNames đã sẵn sàng.
     */
    private void applyPendingGenreSelection() {
        if (pendingGenreNames == null || loadedGenres == null) return;
        Set<Integer> idsToSelect = new HashSet<>();
        for (GenreResponse genre : loadedGenres) {
            if (pendingGenreNames.contains(genre.getName())) {
                idsToSelect.add(genre.getId());
            }
        }
        genreAdapter.setSelectedIds(idsToSelect);
        pendingGenreNames = null;
    }

    private void loadMangaData(int id) {
        adminRepository.getMangaById(id, new AdminRepository.AdminCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    etTitle.setText(data.getTitle() != null ? data.getTitle() : "");
                    etAuthor.setText(data.getAuthorName() != null ? data.getAuthorName() : "");
                    etArtist.setText(data.getArtistName() != null ? data.getArtistName() : "");
                    etDescription.setText(data.getDescription() != null ? data.getDescription() : "");
                    coverUrl = data.getCoverImageUrl();

                    if (data.getStatus() != null) {
                        switch (data.getStatus()) {
                            case COMPLETED: spinnerStatus.setSelection(1); break;
                            case HIATUS: spinnerStatus.setSelection(2); break;
                            default: spinnerStatus.setSelection(0); break;
                        }
                    }

                    if (coverUrl != null && !coverUrl.isEmpty()) {
                        Glide.with(requireContext()).load(coverUrl)
                                .placeholder(R.drawable.bg_placeholder_cover).into(ivCover);
                    }

                    // Lưu tên genre để pre-select; nếu genres đã load xong thì map ngay
                    if (data.getGenres() != null && !data.getGenres().isEmpty()) {
                        pendingGenreNames = new HashSet<>(data.getGenres());
                        applyPendingGenreSelection();
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadCoverImage(Uri imageUri) {
        Toast.makeText(requireContext(), "Đang upload ảnh...", Toast.LENGTH_SHORT).show();
        uploadRepository.uploadImage(imageUri, "manga_covers").enqueue(new Callback<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>>() {
            @Override
            public void onResponse(@NonNull Call<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> call,
                                   @NonNull Response<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    coverUrl = response.body().getData().imageUrl;
                    requireActivity().runOnUiThread(() -> {
                        Glide.with(requireContext()).load(coverUrl)
                                .placeholder(R.drawable.bg_placeholder_cover).into(ivCover);
                        Toast.makeText(requireContext(), "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Upload ảnh thất bại", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi upload: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveManga() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String artist = etArtist.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên truyện", Toast.LENGTH_SHORT).show();
            return;
        }

        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");

        MangaResponse.MangaStatus status;
        switch (spinnerStatus.getSelectedItemPosition()) {
            case 1: status = MangaResponse.MangaStatus.COMPLETED; break;
            case 2: status = MangaResponse.MangaStatus.HIATUS; break;
            default: status = MangaResponse.MangaStatus.ONGOING; break;
        }

        selectedGenreIds.clear();
        selectedGenreIds.addAll(genreAdapter.getSelectedIds());

        MangaSubmitRequest request = new MangaSubmitRequest(
                title, slug, description, coverUrl, author, artist,
                status, null, selectedGenreIds);

        btnSave.setEnabled(false);

        if (mangaId > 0) {
            mangaRepository.updateMyManga(mangaId, request, new MangaRepository.MangaCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse data) {
                    onSaveSuccess();
                }

                @Override
                public void onError(String message) {
                    onSaveError(message);
                }
            });
        } else {
            adminRepository.createManga(request, new AdminRepository.AdminCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse data) {
                    onSaveSuccess();
                }

                @Override
                public void onError(String message) {
                    onSaveError(message);
                }
            });
        }
    }

    private void onSaveSuccess() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Lưu truyện thành công", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
        });
    }

    private void onSaveError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            btnSave.setEnabled(true);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}
