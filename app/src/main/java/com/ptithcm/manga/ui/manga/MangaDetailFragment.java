package com.ptithcm.manga.ui.manga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.genre.GenreChipAdapter;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MangaDetailFragment extends Fragment {

    private String mangaSlug;
    private MangaRepository mangaRepository;
    

    private ImageView ivCover, btnBack;
    private TextView tvTitle, tvAuthor, tvStatus, tvViews, tvLikes, tvFollows, tvDescription;
    private RecyclerView rvGenres, rvChapters;
    private MaterialButton btnLike, btnFollow, btnStartReading;
    private GenreChipAdapter genreAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mangaSlug = getArguments().getString("mangaSlug");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mangaRepository = new MangaRepository(requireContext());
        initViews(view);
        setupListeners();
        loadMangaDetail();
    }

    private void initViews(View view) {
        ivCover = view.findViewById(R.id.iv_cover);
        btnBack = view.findViewById(R.id.btn_back);
        tvTitle = view.findViewById(R.id.tv_title);
        tvAuthor = view.findViewById(R.id.tv_author);
        tvStatus = view.findViewById(R.id.tv_status);
        tvViews = view.findViewById(R.id.tv_views);
        tvLikes = view.findViewById(R.id.tv_likes);
        tvFollows = view.findViewById(R.id.tv_follows);
        tvDescription = view.findViewById(R.id.tv_description);
        rvGenres = view.findViewById(R.id.rv_genres);
        rvChapters = view.findViewById(R.id.rv_chapters);
        btnLike = view.findViewById(R.id.btn_like);
        btnFollow = view.findViewById(R.id.btn_follow);
        btnStartReading = view.findViewById(R.id.btn_start_reading);

        // Setup Genre RecyclerView
        genreAdapter = new GenreChipAdapter();
        rvGenres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGenres.setAdapter(genreAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void loadMangaDetail() {
        if (mangaSlug == null) return;

        mangaRepository.getMangaBySlug(mangaSlug, new MangaRepository.MangaCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse data) {
                if (!isAdded() || data == null) return;
                requireActivity().runOnUiThread(() -> bindData(data));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), message != null ? message : "Lỗi tải chi tiết truyện", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void bindData(MangaResponse manga) {
        tvTitle.setText(manga.getTitle());
        tvAuthor.setText("Tác giả: " + (manga.getAuthorName() != null ? manga.getAuthorName() : "Đang cập nhật"));
        tvStatus.setText(manga.getStatus() != null ? manga.getStatus().toString() : "UNKNOWN");
        tvViews.setText(String.valueOf(manga.getViewCount() != null ? manga.getViewCount() : 0));
        tvLikes.setText(String.valueOf(manga.getLikeCount() != null ? manga.getLikeCount() : 0));
        tvFollows.setText(String.valueOf(manga.getFollowCount() != null ? manga.getFollowCount() : 0));
        tvDescription.setText(manga.getDescription() != null ? manga.getDescription() : "Không có mô tả.");

        // Hiển thị banner/cover
        Glide.with(this)
                .load(manga.getCoverImageUrl())
                .placeholder(R.drawable.bg_placeholder_cover)
                .error(R.drawable.bg_placeholder_cover)
                .centerCrop()
                .into(ivCover);

        // Hiển thị thể loại
        if (manga.getGenres() != null) {
            List<GenreResponse> genreResponses = new ArrayList<>();
            for (String genreName : manga.getGenres()) {
                genreResponses.add(new GenreResponse(null, genreName, null));
            }
            genreAdapter.setGenres(genreResponses);
        }

        // Kiểm tra totalChapters
        if (manga.getTotalChapters() != null && manga.getTotalChapters() > 0) {
            btnStartReading.setText("Đọc ngay (Chương 1)");
            btnStartReading.setEnabled(true);
        } else {
            btnStartReading.setText("Chưa có chương nào");
            btnStartReading.setEnabled(false);
        }
    }
}
