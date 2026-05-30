package com.ptithcm.manga.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import com.google.android.material.button.MaterialButton;
import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.GenreSelectAdapter;
import com.ptithcm.manga.data.api.CloudinaryApi;
import com.ptithcm.manga.data.model.Genre;
import com.ptithcm.manga.data.model.request.MangaRequest;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.repository.GenreRepository;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminMangaFormFragment extends Fragment {

    private AdminRepository mangaRepository;
    private GenreRepository genreRepository;
    
    private EditText etTitle, etAuthor, etArtist, etDescription;
    private Spinner spinnerStatus;
    private FrameLayout coverContainer;
    private MaterialButton btnSave;
    private TextView tvFormTitle;
    
    private GenreSelectAdapter genreAdapter;
    private Uri selectedCoverUri;
    private String uploadedCoverUrl;
    
    private Integer editMangaId = null; // null if create, else edit

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedCoverUri = uri;
                    // Usually we'd show a preview here in an ImageView, but container is FrameLayout.
                    // For simplicity, we just change the text
                    ((TextView) coverContainer.getChildAt(0)).setText("Đã chọn ảnh bìa");
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manga_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mangaRepository = new AdminRepository(requireContext());
        genreRepository = new GenreRepository(requireContext());
        
        initViews(view);
        
        if (getArguments() != null && getArguments().containsKey("mangaId")) {
            editMangaId = getArguments().getInt("mangaId");
            tvFormTitle.setText("Sửa truyện");
            loadMangaDetail();
        }

        loadGenres();
    }
    
    private void initViews(View view) {
        tvFormTitle = view.findViewById(R.id.tv_form_title);
        etTitle = view.findViewById(R.id.et_title);
        etAuthor = view.findViewById(R.id.et_author);
        etArtist = view.findViewById(R.id.et_artist);
        etDescription = view.findViewById(R.id.et_description);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        coverContainer = view.findViewById(R.id.cover_container);
        btnSave = view.findViewById(R.id.btn_save);

        String[] statuses = {"ONGOING", "COMPLETED", "HIATUS"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        RecyclerView rvGenres = view.findViewById(R.id.rv_genres);
        rvGenres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        genreAdapter = new GenreSelectAdapter();
        rvGenres.setAdapter(genreAdapter);

        coverContainer.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSave.setOnClickListener(v -> saveManga());
    }

    private void loadGenres() {
        genreRepository.getAllGenres(new GenreRepository.RepositoryCallback<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> result) {
                genreAdapter.setGenres(result);
            }
            @Override
            public void onError(String message) {}
        });
    }

    private void loadMangaDetail() {
        mangaRepository.getMangaDetail(String.valueOf(editMangaId), new AdminRepository.RepositoryCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse result) {
                etTitle.setText(result.getTitle());
                etAuthor.setText(result.getAuthorName());
                etArtist.setText(result.getArtistName());
                etDescription.setText(result.getDescription());
                uploadedCoverUrl = result.getCoverImageUrl();
                if (uploadedCoverUrl != null) {
                    ((TextView) coverContainer.getChildAt(0)).setText("Đã có ảnh bìa");
                }
                // TODO: set status selection, genre selection based on result
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveManga() {
        if (etTitle.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên truyện", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        if (selectedCoverUri != null) {
            CloudinaryApi.uploadImage(requireContext(), selectedCoverUri, new CloudinaryApi.UploadCallback() {
                @Override
                public void onSuccess(String url) {
                    uploadedCoverUrl = url;
                    submitData();
                }
                @Override
                public void onError(String message) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            submitData();
        }
    }

    private void submitData() {
        MangaRequest req = new MangaRequest();
        req.setTitle(etTitle.getText().toString());
        req.setAuthorName(etAuthor.getText().toString());
        req.setArtistName(etArtist.getText().toString());
        req.setDescription(etDescription.getText().toString());
        req.setCoverImageUrl(uploadedCoverUrl);
        req.setStatus(spinnerStatus.getSelectedItem().toString());
        req.setGenreIds(new ArrayList<>(genreAdapter.getSelectedGenreIds()));

        if (editMangaId == null) {
            // Admin Add Manga
            mangaRepository.adminCreateManga(req, new AdminRepository.RepositoryCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse result) {
                    Toast.makeText(getContext(), "Thêm truyện thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
                @Override
                public void onError(String message) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Admin Edit Manga
            mangaRepository.adminUpdateManga(editMangaId, req, new AdminRepository.RepositoryCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse result) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
                @Override
                public void onError(String message) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
