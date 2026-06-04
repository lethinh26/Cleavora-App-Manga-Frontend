package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.admin.AdminMangaAdapter;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminMangaListFragment extends Fragment implements AdminMangaAdapter.AdminMangaListener {

    private AdminRepository adminRepository;
    private RecyclerView rvMangas;
    private SwipeRefreshLayout swipeRefresh;
    private AdminMangaAdapter adapter;

    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLoading = false;
    private String currentFilter = null; // null = all, APPROVED, PENDING, REJECTED
    private final List<MangaResponse> mangaList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manga_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminRepository = new AdminRepository(requireContext());

        rvMangas = view.findViewById(R.id.rv_mangas);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        View btnAddManga = view.findViewById(R.id.btn_add_manga);

        // Filter chips
        View chipAll = view.findViewById(R.id.chip_all);
        View chipApproved = view.findViewById(R.id.chip_approved);
        View chipPending = view.findViewById(R.id.chip_pending);
        View chipRejected = view.findViewById(R.id.chip_rejected);

        adapter = new AdminMangaAdapter(this);
        rvMangas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMangas.setAdapter(adapter);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                currentPage = 0;
                loadMangas();
            });
        }

        rvMangas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && dy > 0 && currentPage < totalPages - 1) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (lm != null && lm.findLastVisibleItemPosition() >= adapter.getItemCount() - 3) {
                        loadMore();
                    }
                }
            }
        });

        // Filter chip listeners
        if (chipAll != null) chipAll.setOnClickListener(v -> setFilter(null, chipAll, chipApproved, chipPending, chipRejected));
        if (chipApproved != null) chipApproved.setOnClickListener(v -> setFilter("APPROVED", chipAll, chipApproved, chipPending, chipRejected));
        if (chipPending != null) chipPending.setOnClickListener(v -> setFilter("PENDING", chipAll, chipApproved, chipPending, chipRejected));
        if (chipRejected != null) chipRejected.setOnClickListener(v -> setFilter("REJECTED", chipAll, chipApproved, chipPending, chipRejected));

        btnAddManga.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("mangaId", -1); // create mode
            Navigation.findNavController(v).navigate(R.id.action_admin_manga_to_form, args);
        });

        loadMangas();
    }

    private void setFilter(String filter, View... chips) {
        currentFilter = filter;
        // Highlight active chip
        for (View chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip);
            // Simple toggle - use tag to identify active
        }
        currentPage = 0;
        loadMangas();
    }

    private void loadMangas() {
        isLoading = true;
        currentPage = 0;
        mangaList.clear();

        adminRepository.getAdminMangas(0, 20, currentFilter, new AdminRepository.AdminCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    mangaList.addAll(data.getContent());
                    totalPages = data.getTotalPages();
                    adapter.setItems(mangaList);
                    isLoading = false;
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        int nextPage = currentPage + 1;

        adminRepository.getAdminMangas(nextPage, 20, currentFilter, new AdminRepository.AdminCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    mangaList.addAll(data.getContent());
                    currentPage = nextPage;
                    adapter.setItems(mangaList);
                    isLoading = false;
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }
        });
    }

    @Override
    public void onClick(int mangaId) {
        Bundle args = new Bundle();
        args.putInt("mangaId", mangaId);
        Navigation.findNavController(requireView()).navigate(R.id.action_admin_manga_to_form, args);
    }

    @Override
    public void onDelete(int mangaId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa truyện")
                .setMessage("Xóa truyện này? Tất cả chapters, likes, follows, history sẽ bị xóa.")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    adminRepository.deleteManga(mangaId, new AdminRepository.AdminCallback<Object>() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                adapter.removeItem(mangaId);
                                Toast.makeText(requireContext(), "Đã xóa truyện", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onManageChapters(int mangaId) {
        Bundle args = new Bundle();
        args.putInt("mangaId", mangaId);
        Navigation.findNavController(requireView()).navigate(R.id.action_admin_manga_to_chapter_form, args);
    }
}
