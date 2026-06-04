package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.admin.PendingMangaAdapter;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminPendingFragment extends Fragment implements PendingMangaAdapter.PendingMangaListener {

    private AdminRepository adminRepository;
    private RecyclerView rvPending;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private PendingMangaAdapter adapter;

    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLoading = false;
    private final List<MangaResponse> mangaList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminRepository = new AdminRepository(requireContext());

        rvPending = view.findViewById(R.id.rv_pending);
        tvEmpty = view.findViewById(R.id.tv_empty);

        // Wrap in SwipeRefreshLayout
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                currentPage = 0;
                loadPendingMangas();
            });
        }

        adapter = new PendingMangaAdapter(this);
        rvPending.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPending.setAdapter(adapter);

        // Pagination scroll listener
        rvPending.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && dy > 0 && currentPage < totalPages - 1) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - 3) {
                        loadMore();
                    }
                }
            }
        });

        loadPendingMangas();
    }

    private void loadPendingMangas() {
        isLoading = true;
        currentPage = 0;
        mangaList.clear();

        adminRepository.getPendingMangas(0, 20, new AdminRepository.AdminCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    mangaList.addAll(data.getContent());
                    totalPages = data.getTotalPages();
                    currentPage = 0;
                    adapter.setItems(mangaList);
                    updateEmptyState();
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
                    updateEmptyState();
                });
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        int nextPage = currentPage + 1;

        adminRepository.getPendingMangas(nextPage, 20, new AdminRepository.AdminCallback<PageResponse<MangaResponse>>() {
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
    public void onApprove(int mangaId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận duyệt")
                .setMessage("Duyệt truyện này? Truyện sẽ hiển thị công khai.")
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    adminRepository.approveManga(mangaId, new AdminRepository.AdminCallback<MangaResponse>() {
                        @Override
                        public void onSuccess(MangaResponse data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                adapter.removeItem(mangaId);
                                Toast.makeText(requireContext(), "Đã duyệt truyện", Toast.LENGTH_SHORT).show();
                                updateEmptyState();
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
    public void onReject(int mangaId) {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.reject_reason);
        input.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(requireContext())
                .setTitle("Từ chối truyện")
                .setView(input)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng nhập lý do từ chối", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    adminRepository.rejectManga(mangaId, reason, new AdminRepository.AdminCallback<MangaResponse>() {
                        @Override
                        public void onSuccess(MangaResponse data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                adapter.removeItem(mangaId);
                                Toast.makeText(requireContext(), "Đã từ chối truyện", Toast.LENGTH_SHORT).show();
                                updateEmptyState();
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

    private void updateEmptyState() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }
}
