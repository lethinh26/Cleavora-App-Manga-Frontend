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
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.FollowResponse;
import com.ptithcm.manga.data.model.response.FollowStatusResponse;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import com.ptithcm.manga.adapter.manga.ChapterAdapter;
import com.ptithcm.manga.data.model.response.ChapterResponse;
import com.ptithcm.manga.data.repository.ChapterRepository;
import com.ptithcm.manga.ui.reader.ReaderActivity;

public class MangaDetailFragment extends Fragment {

    private String mangaSlug;
    private int mangaId = -1;

    private MangaRepository mangaRepository;
    private TokenManager tokenManager;

    private ImageView ivCover, btnBack;
    private TextView tvTitle, tvAuthor, tvStatus, tvViews, tvLikes, tvFollows, tvDescription;
    private RecyclerView rvGenres, rvChapters;
    private MaterialButton btnLike, btnFollow, btnStartReading;
    private GenreChipAdapter genreAdapter;
    private ChapterRepository chapterRepository;
    private ChapterAdapter chapterAdapter;

    private boolean isLiked = false;
    private boolean isFollowed = false;
    private int likeCount = 0;
    private int followCount = 0;
    private boolean userStatusLoaded = false;
    private List<ChapterResponse> currentChapters = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mangaSlug = getArguments().getString("mangaSlug");
            mangaId = getArguments().getInt("mangaId", -1);
            if ((mangaSlug == null || mangaSlug.trim().isEmpty()) && getArguments().containsKey("mangaSlug")) {
                mangaSlug = getArguments().getString("mangaSlug");
            }
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
        tokenManager = TokenManager.getInstance(requireContext());
        chapterRepository = new ChapterRepository(requireContext());

        initViews(view);
        setupListeners();
        setupAuthState();

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

        genreAdapter = new GenreChipAdapter();
        rvGenres.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        rvGenres.setAdapter(genreAdapter);

        chapterAdapter = new ChapterAdapter(chapter -> {
            Intent intent = new Intent(requireContext(), ReaderActivity.class);
            intent.putExtra("CHAPTER_ID", chapter.getId());
            startActivity(intent);
        });
        rvChapters.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChapters.setAdapter(chapterAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        btnLike.setOnClickListener(v -> {
            if (!ensureLoggedIn() || !ensureMangaId()) return;
            toggleLike();
        });

        btnFollow.setOnClickListener(v -> {
            if (!ensureLoggedIn() || !ensureMangaId()) return;
            toggleFollow();
        });

        btnStartReading.setOnClickListener(v -> openFirstChapter());
    }

    private void setupAuthState() {
        if (!tokenManager.isLoggedIn()) {
            btnLike.setEnabled(false);
            btnLike.setText("Đăng nhập để thích");

            btnFollow.setEnabled(false);
            btnFollow.setText("Đăng nhập để theo dõi");
        }
    }

    private boolean ensureLoggedIn() {
        if (tokenManager.isLoggedIn()) return true;

        Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean ensureMangaId() {
        if (mangaId > 0) return true;

        Toast.makeText(requireContext(), "Không xác định được truyện", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void loadDependentData() {
        loadUserStatusIfPossible();
        loadChapters();
    }

    private void loadMangaDetail() {
        if (mangaSlug == null || mangaSlug.trim().isEmpty()) return;

        mangaRepository.getMangaBySlug(mangaSlug, new MangaRepository.MangaCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse data) {
                runOnUiThreadSafe(() -> {
                    if (data == null) return;
                    bindData(data);
                    loadDependentData();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThreadSafe(() ->
                        Toast.makeText(
                                getContext(),
                                message != null ? message : "Lỗi tải chi tiết truyện",
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }
        });
    }

    private void bindData(MangaResponse manga) {
        if (manga.getId() != null && manga.getId() > 0) {
            mangaId = manga.getId();
        }

        tvTitle.setText(manga.getTitle());
        tvAuthor.setText("Tác giả: " +
                (manga.getAuthorName() != null ? manga.getAuthorName() : "Đang cập nhật"));
        tvStatus.setText(manga.getStatus() != null ? manga.getStatus().toString() : "UNKNOWN");

        likeCount = manga.getLikeCount() != null ? manga.getLikeCount() : 0;
        followCount = manga.getFollowCount() != null ? manga.getFollowCount() : 0;

        tvViews.setText(String.valueOf(manga.getViewCount() != null ? manga.getViewCount() : 0));
        tvLikes.setText(String.valueOf(likeCount));
        tvFollows.setText(String.valueOf(followCount));

        tvDescription.setText(
                manga.getDescription() != null ? manga.getDescription() : "Không có mô tả."
        );

        Glide.with(this)
                .load(manga.getCoverImageUrl())
                .placeholder(R.drawable.bg_placeholder_cover)
                .error(R.drawable.bg_placeholder_cover)
                .centerCrop()
                .into(ivCover);

        if (manga.getGenres() != null) {
            List<GenreResponse> genreResponses = new ArrayList<>();
            for (String genreName : manga.getGenres()) {
                genreResponses.add(new GenreResponse(null, genreName, null));
            }
            genreAdapter.setGenres(genreResponses);
        }

        btnStartReading.setText("Đang tải chương...");
        btnStartReading.setEnabled(false);
    }

    private void loadChapters() {
        if (mangaId <= 0) return;
        chapterRepository.getChaptersByMangaId(mangaId, new ChapterRepository.RepositoryCallback<List<ChapterResponse>>() {
            @Override
            public void onSuccess(List<ChapterResponse> result) {
                runOnUiThreadSafe(() -> {
                    currentChapters = result != null ? result : new ArrayList<>();
                    chapterAdapter.setChapters(currentChapters);
                    updateStartReadingButton();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThreadSafe(() -> {
                    currentChapters = new ArrayList<>();
                    updateStartReadingButton();
                    Toast.makeText(getContext(), "Lỗi tải chapters: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateStartReadingButton() {
        if (currentChapters != null && !currentChapters.isEmpty()) {
            ChapterResponse chapter = currentChapters.get(currentChapters.size() - 1);
            btnStartReading.setText("Đọc ngay (Chương " + chapter.getChapterNumber() + ")");
            btnStartReading.setEnabled(true);
        } else {
            btnStartReading.setText("Chưa có chương nào");
            btnStartReading.setEnabled(false);
        }
    }

    private void openFirstChapter() {
        if (currentChapters == null || currentChapters.isEmpty()) return;
        ChapterResponse chapter = currentChapters.get(currentChapters.size() - 1);
        Intent intent = new Intent(requireContext(), ReaderActivity.class);
        intent.putExtra("CHAPTER_ID", chapter.getId());
        startActivity(intent);
    }


    private void loadUserStatusIfPossible() {
        if (!tokenManager.isLoggedIn()) return;
        if (mangaId <= 0) return;
        if (userStatusLoaded) return;

        userStatusLoaded = true;
        loadLikeStatus();
        loadFollowStatus();
    }

    // ============ LIKE ============

    private void loadLikeStatus() {
        mangaRepository.getLikeStatus(mangaId, new MangaRepository.MangaCallback<LikeStatusResponse>() {
            @Override
            public void onSuccess(LikeStatusResponse data) {
                runOnUiThreadSafe(() -> {
                    if (data == null) return;
                    isLiked = data.isLiked();
                    updateLikeButton();
                });
            }

            @Override
            public void onError(String message) {
                // Không chặn màn hình chi tiết nếu load trạng thái like lỗi
            }
        });
    }

    private void toggleLike() {
        btnLike.setEnabled(false);

        mangaRepository.toggleLike(mangaId, new MangaRepository.MangaCallback<LikeResponse>() {
            @Override
            public void onSuccess(LikeResponse data) {
                runOnUiThreadSafe(() -> {
                    if (data == null) return;

                    isLiked = data.isLiked();
                    likeCount = data.getLikeCount();

                    updateLikeButton();
                    tvLikes.setText(String.valueOf(likeCount));
                    btnLike.setEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThreadSafe(() -> {
                    Toast.makeText(
                            requireContext(),
                            message != null ? message : "Không thể cập nhật yêu thích",
                            Toast.LENGTH_SHORT
                    ).show();
                    btnLike.setEnabled(true);
                });
            }
        });
    }

    private void updateLikeButton() {
        if (isLiked) {
            btnLike.setIconResource(android.R.drawable.btn_star_big_on);
            btnLike.setText("Đã thích");
        } else {
            btnLike.setIconResource(android.R.drawable.btn_star_big_off);
            btnLike.setText("Yêu thích");
        }
    }

    // ============ FOLLOW ============

    private void loadFollowStatus() {
        mangaRepository.getFollowStatus(mangaId, new MangaRepository.MangaCallback<FollowStatusResponse>() {
            @Override
            public void onSuccess(FollowStatusResponse data) {
                runOnUiThreadSafe(() -> {
                    if (data == null) return;
                    isFollowed = data.isFollowed();
                    updateFollowButton();
                });
            }

            @Override
            public void onError(String message) {
                // Không chặn màn hình chi tiết nếu load trạng thái follow lỗi
            }
        });
    }

    private void toggleFollow() {
        btnFollow.setEnabled(false);

        mangaRepository.toggleFollow(mangaId, new MangaRepository.MangaCallback<FollowResponse>() {
            @Override
            public void onSuccess(FollowResponse data) {
                runOnUiThreadSafe(() -> {
                    if (data == null) return;

                    isFollowed = data.isFollowed();
                    followCount = data.getFollowCount();

                    updateFollowButton();
                    tvFollows.setText(String.valueOf(followCount));
                    btnFollow.setEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThreadSafe(() -> {
                    Toast.makeText(
                            requireContext(),
                            message != null ? message : "Không thể cập nhật theo dõi",
                            Toast.LENGTH_SHORT
                    ).show();
                    btnFollow.setEnabled(true);
                });
            }
        });
    }

    private void updateFollowButton() {
        if (isFollowed) {
            btnFollow.setIconResource(android.R.drawable.btn_star_big_on);
            btnFollow.setText("Đang theo dõi");
        } else {
            btnFollow.setIconResource(android.R.drawable.btn_star_big_off);
            btnFollow.setText("Theo dõi");
        }
    }

    private void runOnUiThreadSafe(Runnable action) {
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            if (isAdded()) {
                action.run();
            }
        });
    }
}