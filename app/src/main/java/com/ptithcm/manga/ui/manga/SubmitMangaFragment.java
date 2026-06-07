package com.ptithcm.manga.ui.manga;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Locale;
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

        genreAdapter = new MultiSelectGenreAdapter();
        rvGenres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGenres.setAdapter(genreAdapter);
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

    private void loadGenres() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> genreAdapter.setGenres(data));
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

    private void handleSubmit() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || selectedImageUri == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin và chọn ảnh bìa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (genreAdapter.getSelectedIds().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một thể loại", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang xử lý...");

        // 1. Upload cover image to Cloudinary
        cloudRepository.uploadImageToCloudinary(requireContext(), selectedImageUri, new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String coverUrl = response.body().imageUrl;
                    // 2. Submit manga details with the uploaded URL
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
    }

    private void performSubmit(String title, String author, String description, String coverUrl) {
        MangaSubmitRequest request = new MangaSubmitRequest();
        request.setTitle(title);
        request.setSlug(toSlug(title));
        request.setAuthorName(author);
        request.setArtistName(author); // Artist defaults to author
        request.setDescription(description);
        request.setCoverImageUrl(coverUrl);
        request.setStatus(MangaResponse.MangaStatus.ONGOING);
        request.setGenreIds(genreAdapter.getSelectedIds());

        mangaRepository.submitManga(request, new MangaRepository.MangaCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Đăng truyện thành công, vui lòng chờ duyệt!", Toast.LENGTH_LONG).show();
                    // Navigate back to My Mangas screen
                    Navigation.findNavController(requireView()).navigate(R.id.nav_my_mangas);
                });
            }

            @Override
            public void onError(String message) {
                handleError("Lỗi đăng truyện: " + message);
            }
        });
    }

    private void handleError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            btnSubmit.setEnabled(true);
            btnSubmit.setText("ĐĂNG TRUYỆN");
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
