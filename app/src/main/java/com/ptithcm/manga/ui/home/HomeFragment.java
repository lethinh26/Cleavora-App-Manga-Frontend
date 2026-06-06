package com.ptithcm.manga.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.BannerAdapter;
import com.ptithcm.manga.adapter.manga.MangaCardAdapter;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MangaCardAdapter.OnMangaClickListener {

    private MangaRepository mangaRepository;

    // Banner
    private ViewPager2 vpBanner;
    private LinearLayout bannerDots;
    private View bannerContainer;
    private BannerAdapter bannerAdapter;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private static final long BANNER_DELAY_MS = 3000;

    // Content
    private RecyclerView rvLatest, rvPopular, rvCompleted;
    private TextView tvLatestHeader, tvPopularHeader, tvCompletedHeader;
    private Spinner spinnerGenres;

    private MangaCardAdapter latestAdapter, popularAdapter, completedAdapter;
    private List<GenreResponse> fullGenreList = new ArrayList<>();
    private boolean isFirstSelection = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());

        // Bind views
        rvLatest = view.findViewById(R.id.rv_latest);
        rvPopular = view.findViewById(R.id.rv_popular);
        rvCompleted = view.findViewById(R.id.rv_completed);
        tvLatestHeader = view.findViewById(R.id.tv_latest_header);
        tvPopularHeader = view.findViewById(R.id.tv_popular_header);
        tvCompletedHeader = view.findViewById(R.id.tv_completed_header);
        bannerContainer = view.findViewById(R.id.banner_container);
        vpBanner = view.findViewById(R.id.vp_banner);
        bannerDots = view.findViewById(R.id.banner_dots);
        spinnerGenres = view.findViewById(R.id.spinner_genres);

        setupRecyclerViews();
        setupBanner();
        setupGenreSpinner();
        loadDefaultData();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannerAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBannerAutoScroll();
    }

    // ── Banner ──────────────────────────────────────────────────────────────

    private void setupBanner() {
        bannerAdapter = new BannerAdapter(manga -> {
            Bundle bundle = new Bundle();
            bundle.putString("mangaSlug", manga.getSlug());
            bundle.putInt("mangaId", manga.getId() != null ? manga.getId() : -1);
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_manga_detail, bundle);
        });
        vpBanner.setAdapter(bannerAdapter);

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });
    }

    private void setBannerData(List<MangaResponse> mangas) {
        // Show top 5 as banner items
        int count = Math.min(mangas.size(), 5);
        List<MangaResponse> bannerItems = mangas.subList(0, count);
        bannerAdapter.setItems(bannerItems);
        buildDots(count);

        if (count > 0) {
            bannerContainer.setVisibility(View.VISIBLE);
            startBannerAutoScroll();
        }
    }

    private void buildDots(int count) {
        bannerDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            int size = 8; // dp
            int sizeInPx = (int) (size * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0
                    ? R.drawable.dot_active
                    : R.drawable.dot_inactive);
            bannerDots.addView(dot);
        }
    }

    private void updateDots(int activePosition) {
        if (bannerDots == null) return;
        for (int i = 0; i < bannerDots.getChildCount(); i++) {
            bannerDots.getChildAt(i).setBackgroundResource(
                    i == activePosition ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void startBannerAutoScroll() {
        stopBannerAutoScroll();
        if (bannerAdapter == null || bannerAdapter.getItemCount() <= 1) return;
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || vpBanner == null) return;
                int next = (vpBanner.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                vpBanner.setCurrentItem(next, true);
                bannerHandler.postDelayed(this, BANNER_DELAY_MS);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
    }

    private void stopBannerAutoScroll() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerRunnable = null;
        }
    }

    // ── RecyclerViews ────────────────────────────────────────────────────────

    private void setupRecyclerViews() {
        latestAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvLatest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvLatest.setAdapter(latestAdapter);

        popularAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopular.setAdapter(popularAdapter);

        completedAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvCompleted.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCompleted.setAdapter(completedAdapter);
    }

    // ── Genre Spinner ────────────────────────────────────────────────────────

    private void setupGenreSpinner() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded() || data == null) return;
                fullGenreList = data;

                List<String> names = new ArrayList<>();
                names.add("Tất cả thể loại");
                for (GenreResponse g : data) names.add(g.getName());

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenres.setAdapter(adapter);

                    spinnerGenres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                            if (isFirstSelection) { isFirstSelection = false; return; }
                            if (position == 0) {
                                loadDefaultData();
                            } else if (position - 1 < fullGenreList.size()) {
                                GenreResponse sel = fullGenreList.get(position - 1);
                                filterByGenre(sel.getSlug(), sel.getName());
                            }
                        }
                        @Override public void onNothingSelected(AdapterView<?> parent) {}
                    });
                });
            }
            @Override
            public void onError(String message) { showError(message); }
        });
    }

    // ── Data loading ─────────────────────────────────────────────────────────

    private void loadDefaultData() {
        mangaRepository.getMangas(new MangaRepository.MangaCallback<List<MangaResponse>>() {
            @Override
            public void onSuccess(List<MangaResponse> data) {
                if (!isAdded() || data == null) return;
                requireActivity().runOnUiThread(() -> {
                    // Banner: use the first 5 most recent mangas
                    setBannerData(data);

                    tvLatestHeader.setText("Mới cập nhật");
                    setOtherSectionsVisible(true);

                    latestAdapter.updateData(data);
                    popularAdapter.updateData(data);

                    List<MangaResponse> completed = new ArrayList<>();
                    for (MangaResponse m : data) {
                        if (m.getStatus() == MangaResponse.MangaStatus.COMPLETED) {
                            completed.add(m);
                        }
                    }
                    completedAdapter.updateData(completed);
                });
            }
            @Override
            public void onError(String message) { showError(message); }
        });
    }

    private void filterByGenre(String slug, String genreName) {
        mangaRepository.getMangasByGenre(slug, new MangaRepository.MangaCallback<List<MangaResponse>>() {
            @Override
            public void onSuccess(List<MangaResponse> data) {
                if (!isAdded() || data == null) return;
                requireActivity().runOnUiThread(() -> {
                    tvLatestHeader.setText("Kết quả: " + genreName);
                    latestAdapter.updateData(data);
                    setOtherSectionsVisible(false);
                });
            }
            @Override
            public void onError(String message) { showError(message); }
        });
    }

    private void setOtherSectionsVisible(boolean visible) {
        int v = visible ? View.VISIBLE : View.GONE;
        if (rvPopular != null) rvPopular.setVisibility(v);
        if (tvPopularHeader != null) tvPopularHeader.setVisibility(v);
        if (rvCompleted != null) rvCompleted.setVisibility(v);
        if (tvCompletedHeader != null) tvCompletedHeader.setVisibility(v);
    }

    private void showError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(),
                        message != null ? message : "Lỗi kết nối", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onMangaClick(MangaResponse manga) {
        Bundle bundle = new Bundle();
        bundle.putString("mangaSlug", manga.getSlug());
        if (manga.getId() != null) bundle.putInt("mangaId", manga.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_home_to_manga_detail, bundle);
    }
}
