package com.ptithcm.manga.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.FollowListResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

public class FollowingTabFragment extends Fragment {

    private RecyclerView rvFollows;
    private TextView tvEmpty;

    private MangaRepository mangaRepository;
    private MangaCardAdapter adapter;
    private GridLayoutManager layoutManager;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_tab, container, false);
        rvFollows = view.findViewById(R.id.rv_favorites);
        tvEmpty = view.findViewById(R.id.tv_empty);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());

        adapter = new MangaCardAdapter(mangaId -> {
            if (mangaId <= 0) return;
            Bundle args = new Bundle();
            args.putInt("mangaId", mangaId);
            // Dùng Activity NavController thay vì requireView() — tab fragment không có
            // NavController riêng vì chúng không phải node trong nav_graph
            try {
                androidx.navigation.NavController navController =
                        androidx.navigation.Navigation.findNavController(
                                requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_library_to_manga_detail, args);
            } catch (IllegalStateException e) {
                android.util.Log.e("FollowingTab", "NavController error: " + e.getMessage());
            }
        });

        layoutManager = new GridLayoutManager(requireContext(), 3);
        rvFollows.setLayoutManager(layoutManager);
        rvFollows.setAdapter(adapter);

        rvFollows.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && !isLastPage) {
                    int visible = layoutManager.getChildCount();
                    int total = layoutManager.getItemCount();
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    if (visible + firstVisible >= total - 2) {
                        loadNextPage();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload từ đầu mỗi khi vào tab
        if (!TokenManager.getInstance(requireContext()).isLoggedIn()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFollows.setVisibility(View.GONE);
            return;
        }
        resetAndLoad();
    }

    private void resetAndLoad() {
        currentPage = 0;
        isLastPage = false;
        isLoading = false;
        adapter.clear();
        tvEmpty.setVisibility(View.GONE);
        rvFollows.setVisibility(View.VISIBLE);
        loadNextPage();
    }

    private void loadNextPage() {
        if (isLoading || isLastPage) return;
        isLoading = true;

        int pageToLoad = currentPage;

        mangaRepository.getFollows(pageToLoad, PAGE_SIZE, new MangaRepository.MangaCallback<FollowListResponse>() {
            @Override
            public void onSuccess(FollowListResponse data) {
                if (!isAdded()) return;

                isLoading = false;

                if (data == null || data.getContent() == null || data.getContent().isEmpty()) {
                    isLastPage = true;
                    updateEmptyState();
                    return;
                }

                adapter.addMangas(data.getContent());
                isLastPage = data.isLast();
                currentPage = pageToLoad + 1;
                updateEmptyState();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                isLoading = false;
                Toast.makeText(requireContext(),
                        "Lỗi tải theo dõi: " + message, Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFollows.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFollows.setVisibility(View.VISIBLE);
        }
    }
}
