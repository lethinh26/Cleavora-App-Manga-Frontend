package com.ptithcm.manga.ui.reader;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.ReaderAdapter;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.ChapterDetailResponse;
import com.ptithcm.manga.data.model.response.ChapterResponse;
import com.ptithcm.manga.data.model.response.ReadingHistoryResponse;
import com.ptithcm.manga.data.repository.ChapterRepository;
import com.ptithcm.manga.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    private RecyclerView rvPages;
    private LinearLayout topBar, bottomBar;
    private TextView tvChapterTitle, tvPageIndicator;
    private ImageView btnBack, btnPrevChapter, btnNextChapter;
    private ChapterRepository chapterRepository;
    private UserRepository userRepository;
    private ReaderAdapter adapter;
    private boolean isBarVisible = true;

    private int mangaId;
    private int chapterId;          // chapterId từ Intent hoặc button click
    private int currentlyLoadedChapterId = -1;  // chapter đang thực sự hiển thị
    private int totalPages = 0;

    // Chapter navigation
    private List<ChapterResponse> allChapters = new ArrayList<>();
    private int currentChapterIndex = -1;

    // Debounce auto-save
    private final Handler debounceHandler = new Handler();
    private Runnable saveRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        // Fullscreen immersive mode
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        // Setup Views
        rvPages = findViewById(R.id.rv_pages);
        topBar = findViewById(R.id.top_bar);
        bottomBar = findViewById(R.id.bottom_bar);
        tvChapterTitle = findViewById(R.id.tv_chapter_title);
        tvPageIndicator = findViewById(R.id.tv_page_indicator);
        btnBack = findViewById(R.id.btn_back);
        btnPrevChapter = findViewById(R.id.btn_prev_chapter);
        btnNextChapter = findViewById(R.id.btn_next_chapter);

        chapterRepository = new ChapterRepository(this);
        userRepository = new UserRepository(this);
        adapter = new ReaderAdapter(this, this::toggleBars);
        rvPages.setLayoutManager(new LinearLayoutManager(this));
        rvPages.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        // Get Chapter ID + Manga ID from Intent
        chapterId = getIntent().getIntExtra("CHAPTER_ID", -1);
        mangaId = getIntent().getIntExtra("MANGA_ID", -1);
        int lastPage = getIntent().getIntExtra("LAST_PAGE", 0);

        if (chapterId != -1) {
            loadChapter(chapterId, lastPage);
            chapterRepository.incrementViewCount(chapterId, new ChapterRepository.RepositoryCallback<String>() {
                @Override
                public void onSuccess(String result) {}
                @Override
                public void onError(String message) {}
            });
        }

        // Load all chapters for this manga to enable chapter navigation
        if (mangaId != -1) {
            chapterRepository.getChaptersByMangaId(mangaId, new ChapterRepository.RepositoryCallback<List<ChapterResponse>>() {
                @Override
                public void onSuccess(List<ChapterResponse> result) {
                    if (result == null) return;
                    allChapters = result;
                    // Find index of current chapter
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).getId() == chapterId) {
                            currentChapterIndex = i;
                            break;
                        }
                    }
                    updateNavButtons();
                }

                @Override
                public void onError(String message) {
                    // Silent fail — navigation buttons stay disabled
                }
            });
        }

        btnPrevChapter.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                // Lưu progress chương hiện tại trước khi chuyển
                saveProgressImmediately();
                currentChapterIndex--;
                chapterId = allChapters.get(currentChapterIndex).getId();
                loadChapter(chapterId, 0);
                updateNavButtons();
                chapterRepository.incrementViewCount(chapterId, new ChapterRepository.RepositoryCallback<String>() {
                    @Override public void onSuccess(String result) {}
                    @Override public void onError(String message) {}
                });
            }
        });

        btnNextChapter.setOnClickListener(v -> {
            if (currentChapterIndex >= 0 && currentChapterIndex < allChapters.size() - 1) {
                // Lưu progress chương hiện tại trước khi chuyển
                saveProgressImmediately();
                currentChapterIndex++;
                chapterId = allChapters.get(currentChapterIndex).getId();
                loadChapter(chapterId, 0);
                updateNavButtons();
                chapterRepository.incrementViewCount(chapterId, new ChapterRepository.RepositoryCallback<String>() {
                    @Override public void onSuccess(String result) {}
                    @Override public void onError(String message) {}
                });
            }
        });

        // Scroll listener: detect current page + debounce save
        rvPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updatePageIndicator();
                scheduleSaveProgress();
            }
        });
    }

    private void loadChapter(int chapterId, int scrollToPage) {
        chapterRepository.getChapterDetail(chapterId, new ChapterRepository.RepositoryCallback<ChapterDetailResponse>() {
            @Override
            public void onSuccess(ChapterDetailResponse result) {
                // Cập nhật chapter đang hiển thị
                currentlyLoadedChapterId = chapterId;

                tvChapterTitle.setText(result.getTitle() != null ? result.getTitle() : "Chương " + com.ptithcm.manga.util.ChapterFormatter.format(result.getChapterNumber()));
                if (result.getImages() != null) {
                    adapter.setImages(result.getImages());
                    totalPages = result.getImages().size();
                    tvPageIndicator.setText("1 / " + totalPages);

                    // Scroll to saved position if continuing reading
                    if (scrollToPage > 0 && scrollToPage <= totalPages) {
                        int index = scrollToPage - 1;
                        rvPages.scrollToPosition(index);
                        tvPageIndicator.setText(scrollToPage + " / " + totalPages);
                    }

                    // Lưu ngay khi chapter load xong — đây là điểm đáng tin cậy nhất
                    int pageToSave = (scrollToPage > 0 && scrollToPage <= totalPages) ? scrollToPage : 1;
                    saveProgressNow(currentlyLoadedChapterId, pageToSave);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ReaderActivity.this, "Lỗi tải chapter: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getCurrentPage() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvPages.getLayoutManager();
        if (layoutManager == null) return 0;
        if (totalPages <= 0) return 0;

        // Trang hiện tại = item đang chiếm phần lớn màn hình.
        // Ưu tiên item visible hoàn toàn đầu tiên; nếu không có thì dùng first visible.
        int firstCompletely = layoutManager.findFirstCompletelyVisibleItemPosition();
        int firstVisible = layoutManager.findFirstVisibleItemPosition();

        int page;
        if (firstCompletely != RecyclerView.NO_POSITION) {
            page = firstCompletely + 1;
        } else if (firstVisible != RecyclerView.NO_POSITION) {
            page = firstVisible + 1;
        } else {
            return 0;
        }

        // Chỉ đạt trang cuối khi item cuối visible HOÀN TOÀN
        int lastCompletely = layoutManager.findLastCompletelyVisibleItemPosition();
        if (lastCompletely == totalPages - 1) {
            page = totalPages;
        }

        // Clamp trong [1, totalPages]
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;
        return page;
    }

    private void updatePageIndicator() {
        if (totalPages > 0) {
            int currentPage = getCurrentPage();
            tvPageIndicator.setText(currentPage + " / " + totalPages);
        }
    }

    // Lưu progress với chapterId và page cụ thể
    private void saveProgressNow(int chapterId, int page) {
        if (!TokenManager.getInstance(this).isLoggedIn()) return;
        if (mangaId <= 0 || chapterId <= 0 || page <= 0) return;

        userRepository.saveReadingProgress(mangaId, chapterId, page,
            new UserRepository.UserCallback<ReadingHistoryResponse>() {
                @Override
                public void onSuccess(ReadingHistoryResponse data) {
                    // Saved successfully — no UI feedback needed
                }
                @Override
                public void onError(String message) {
                    // Log lỗi để debug, không làm phiền user
                    android.util.Log.w("ReaderActivity", "Save progress failed: " + message
                            + " | mangaId=" + mangaId + " chapterId=" + chapterId + " page=" + page);
                }
            });
    }

    private void scheduleSaveProgress() {
        if (!TokenManager.getInstance(this).isLoggedIn()) return;
        if (currentlyLoadedChapterId <= 0) return;

        if (saveRunnable != null) {
            debounceHandler.removeCallbacks(saveRunnable);
        }

        final int chapterIdSnapshot = currentlyLoadedChapterId;
        saveRunnable = () -> {
            int currentPage = getCurrentPage();
            if (currentPage > 0) {
                saveProgressNow(chapterIdSnapshot, currentPage);
            }
        };
        debounceHandler.postDelayed(saveRunnable, 2000);
    }

    private void saveProgressImmediately() {
        if (!TokenManager.getInstance(this).isLoggedIn()) return;
        if (currentlyLoadedChapterId <= 0) return;

        int currentPage = getCurrentPage();
        if (currentPage <= 0) currentPage = 1;  // fallback nếu RecyclerView chưa layout

        saveProgressNow(currentlyLoadedChapterId, currentPage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel debounce and save immediately
        if (saveRunnable != null) {
            debounceHandler.removeCallbacks(saveRunnable);
        }
        saveProgressImmediately();
    }

    private void toggleBars() {
        if (isBarVisible) {
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
        isBarVisible = !isBarVisible;
    }

    private void updateNavButtons() {
        boolean hasPrev = currentChapterIndex > 0;
        boolean hasNext = currentChapterIndex >= 0 && currentChapterIndex < allChapters.size() - 1;
        btnPrevChapter.setAlpha(hasPrev ? 1f : 0.3f);
        btnNextChapter.setAlpha(hasNext ? 1f : 0.3f);
        btnPrevChapter.setEnabled(hasPrev);
        btnNextChapter.setEnabled(hasNext);
    }
}
