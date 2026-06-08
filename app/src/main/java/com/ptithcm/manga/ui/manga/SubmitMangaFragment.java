package com.ptithcm.manga.ui.manga;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.genre.MultiSelectGenreAdapter;
import com.ptithcm.manga.data.model.request.MangaSubmitRequest;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.UploadResponse;
import com.ptithcm.manga.data.repository.CloudRepository;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitMangaFragment extends Fragment {

    private MangaRepository mangaRepository;
    private CloudRepository cloudRepository;

    private TextInputEditText etTitle, etAuthor, etDescription;
    private FrameLayout coverContainer;
    private ImageView ivCover;
    private TextView tvPlaceholder;
    private RecyclerView rvGenres;
    private MaterialButton btnSubmit;

    private MultiSelectGenreAdapter genreAdapter;
    private Uri selectedImageUri;

    private Spinner spinnerStatus;
    private TextView tvStatusLabel;
    private final List<String> statusOptions = Arrays.asList("Đang tiến hành", "Đã hoàn thành");
    private MangaResponse.MangaStatus selectedStatus = MangaResponse.MangaStatus.ONGOING;

    private int mangaId = -1;            // -1 = create mode, >=0 = edit mode
    private String existingCoverUrl;     // existing cover URL for edit mode
    private MangaResponse.MangaStatus existingStatus; // existing status for edit mode

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_submit_manga, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());
        cloudRepository = new CloudRepository();

        initViews(view);
        setupListeners();
        checkEditMode();
        loadGenres();
    }

    private void initViews(View view) {
        etTitle = view.findViewById(R.id.et_title);
        etAuthor = view.findViewById(R.id.et_author);
        etDescription = view.findViewById(R.id.et_description);
        coverContainer = view.findViewById(R.id.cover_container);
        ivCover = view.findViewById(R.id.iv_cover);
        tvPlaceholder = view.findViewById(R.id.tv_placeholder);
        rvGenres = view.findViewById(R.id.rv_genres);
        btnSubmit = view.findViewById(R.id.btn_submit);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        tvStatusLabel = view.findViewById(R.id.tv_status_label);

        genreAdapter = new MultiSelectGenreAdapter();
        rvGenres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGenres.setAdapter(genreAdapter);

        // Setup status spinner
        android.widget.ArrayAdapter<String> statusAdapter = new android.widget.ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupListeners() {
        coverContainer.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivCover.setVisibility(View.VISIBLE);
                    tvPlaceholder.setVisibility(View.GONE);
                    Glide.with(this).load(uri).into(ivCover);
                }
            }
    );

    private void checkEditMode() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("mangaId")) {
            mangaId = args.getInt("mangaId", -1);
            existingCoverUrl = args.getString("coverUrl");
            etTitle.setText(args.getString("title", ""));
            etAuthor.setText(args.getString("author", ""));
            etDescription.setText(args.getString("description", ""));
            btnSubmit.setText("CẬP NHẬT TRUYỆN");

            // Show existing cover if available
            if (existingCoverUrl != null && !existingCoverUrl.isEmpty()) {
                ivCover.setVisibility(View.VISIBLE);
                tvPlaceholder.setVisibility(View.GONE);
                Glide.with(this).load(existingCoverUrl).into(ivCover);
            }

            // Parse existing status
            String statusStr = args.getString("status");
            if (statusStr != null) {
                try {
                    existingStatus = MangaResponse.MangaStatus.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    existingStatus = MangaResponse.MangaStatus.ONGOING;
                }
            }

            // Show status spinner in edit mode
            spinnerStatus.setVisibility(View.VISIBLE);
            tvStatusLabel.setVisibility(View.VISIBLE);
            spinnerStatus.setSelection(
                    existingStatus == MangaResponse.MangaStatus.COMPLETED ? 1 : 0);
        }
    }

    private void loadGenres() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    genreAdapter.setGenres(data);

                    // In edit mode, fetch manga by ID to get current genre names
                    if (mangaId > 0) {
                        loadMangaGenres(data);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi tải thể loại: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void loadMangaGenres(List<GenreResponse> allGenres) {
        mangaRepository.getMangaById(mangaId, new MangaRepository.MangaCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse data) {
                if (!isAdded() || data.getGenres() == null) return;
                requireActivity().runOnUiThread(() -> {
                    Set<Integer> idsToSelect = new HashSet<>();
                    for (GenreResponse g : allGenres) {
                        if (data.getGenres().contains(g.getName())) {
                            idsToSelect.add(g.getId());
                        }
                    }
                    genreAdapter.setSelectedIds(idsToSelect);
                });
            }

            @Override
            public void onError(String message) {
                // Silently ignore - genres just won't be pre-selected
            }
        });
    }

    private void handleSubmit() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Allow no cover image in edit mode (use existing)
        if (mangaId < 0 && selectedImageUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn ảnh bìa", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (genreAdapter.getSelectedIds().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một thể loại", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang xử lý...");

        if (selectedImageUri != null) {
            // Upload new cover image to Cloudinary
            cloudRepository.uploadImageToCloudinary(requireContext(), selectedImageUri, new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String coverUrl = response.body().imageUrl;
                        performSubmit(title, author, description, coverUrl);
                    } else {
                        handleError("Lỗi upload ảnh bìa");
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    handleError("Lỗi kết nối khi upload ảnh: " + t.getMessage());
                }
            });
        } else {
            // Edit mode: reuse existing cover
            performSubmit(title, author, description, existingCoverUrl);
        }
    }

    private void performSubmit(String title, String author, String description, String coverUrl) {
        MangaSubmitRequest request = new MangaSubmitRequest();
        request.setTitle(title);
        request.setSlug(toSlug(title));
        request.setAuthorName(author);
        request.setArtistName(author); // Artist defaults to author
        request.setDescription(description);
        request.setCoverImageUrl(coverUrl);

        // Determine status: in edit mode, read from spinner
        if (mangaId > 0 && spinnerStatus.getVisibility() == View.VISIBLE) {
            selectedStatus = spinnerStatus.getSelectedItemPosition() == 1
                    ? MangaResponse.MangaStatus.COMPLETED
                    : MangaResponse.MangaStatus.ONGOING;
        }
        request.setStatus(selectedStatus);
        request.setGenreIds(genreAdapter.getSelectedIds());

        if (mangaId > 0) {
            // Edit mode: update existing manga
            mangaRepository.updateMyManga(mangaId, request, new MangaRepository.MangaCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse data) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Cập nhật truyện thành công!", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(requireView()).navigate(R.id.nav_my_mangas);
                    });
                }

                @Override
                public void onError(String message) {
                    handleError("Lỗi cập nhật truyện: " + message);
                }
            });
        } else {
            // Create mode: submit new manga
            mangaRepository.submitManga(request, new MangaRepository.MangaCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse data) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đăng truyện thành công, vui lòng chờ duyệt!", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(requireView()).navigate(R.id.nav_my_mangas);
                    });
                }

                @Override
                public void onError(String message) {
                    handleError("Lỗi đăng truyện: " + message);
                }
            });
        }
    }

    private void handleError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            btnSubmit.setEnabled(true);
            btnSubmit.setText(mangaId > 0 ? "CẬP NHẬT TRUYỆN" : "ĐĂNG TRUYỆN");
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private String toSlug(String input) {
        String nowhitespace = input.replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(normalized).replaceAll("").toLowerCase(Locale.ENGLISH);
        return result.replaceAll("[^a-z0-9-]", "");
    }
}
