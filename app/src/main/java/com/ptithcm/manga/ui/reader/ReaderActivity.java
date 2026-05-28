package com.ptithcm.manga.ui.reader;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.ReaderAdapter;
import com.ptithcm.manga.data.model.response.ChapterDetailResponse;
import com.ptithcm.manga.data.repository.ChapterRepository;

public class ReaderActivity extends AppCompatActivity {

    private RecyclerView rvPages;
    private LinearLayout topBar, bottomBar;
    private TextView tvChapterTitle, tvPageIndicator;
    private ImageView btnBack, btnPrevChapter, btnNextChapter;
    private ChapterRepository repository;
    private ReaderAdapter adapter;
    private boolean isBarVisible = true;

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

        repository = new ChapterRepository(this);
        adapter = new ReaderAdapter(this, this::toggleBars);
        rvPages.setLayoutManager(new LinearLayoutManager(this));
        rvPages.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        // Get Chapter ID from Intent
        int chapterId = getIntent().getIntExtra("CHAPTER_ID", -1);
        if (chapterId != -1) {
            loadChapter(chapterId);
            repository.incrementViewCount(chapterId, new ChapterRepository.RepositoryCallback<String>() {
                @Override
                public void onSuccess(String result) {}
                @Override
                public void onError(String message) {}
            });
        }
    }

    private void loadChapter(int chapterId) {
        repository.getChapterDetail(chapterId, new ChapterRepository.RepositoryCallback<ChapterDetailResponse>() {
            @Override
            public void onSuccess(ChapterDetailResponse result) {
                tvChapterTitle.setText(result.getTitle() != null ? result.getTitle() : "Chương " + result.getChapterNumber());
                if (result.getImages() != null) {
                    adapter.setImages(result.getImages());
                    tvPageIndicator.setText("1 / " + result.getImages().size());
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ReaderActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });
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
}
