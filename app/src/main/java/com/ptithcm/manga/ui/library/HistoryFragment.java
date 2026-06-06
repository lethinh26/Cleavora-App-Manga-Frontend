package com.ptithcm.manga.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.library.HistoryAdapter;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.ReadingHistoryResponse;
import com.ptithcm.manga.data.repository.MangaRepository;
import com.ptithcm.manga.data.repository.UserRepository;
import com.ptithcm.manga.ui.reader.ReaderActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnHistoryActionListener {

    private RecyclerView rvHistory;
    private LinearLayout emptyState;
    private ImageButton btnDeleteAll;
    private ProgressBar progressBar;

    private UserRepository userRepository;
    private MangaRepository mangaRepository;
    private HistoryAdapter adapter;

    // O1+O7: Cache manga info to avoid N+1 fetch on every resume
    private final Map<Integer, MangaResponse> mangaCache = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rv_history);
        emptyState = view.findViewById(R.id.empty_state);
        btnDeleteAll = view.findViewById(R.id.btn_delete_all);
        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(requireContext());
        mangaRepository = new MangaRepository(requireContext());

        adapter = new HistoryAdapter(this);
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(adapter);

        setupSwipeToDelete();

        btnDeleteAll.setOnClickListener(v -> showDeleteAllDialog());

        loadHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload every time user returns (they may have read new chapters).
        // loadHistory() is safe to call multiple times — it shows loading indicator
        // and replaces adapter contents via setList(), so no duplication.
        if (userRepository != null) {
            loadHistory();
        }
    }

    private void loadHistory() {
        if (!TokenManager.getInstance(requireContext()).isLoggedIn()) {
            showEmptyState();
            return;
        }

        // O5: Show loading indicator
        showLoading();

        userRepository.getReadingHistory(new UserRepository.UserCallback<List<ReadingHistoryResponse>>() {
            @Override
            public void onSuccess(List<ReadingHistoryResponse> data) {
                if (!isAdded()) return;
                hideLoading();

                if (data == null || data.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    adapter.setList(data);
                    fetchMangaDetails(data);
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                hideLoading();
                Toast.makeText(requireContext(), "Lỗi tải lịch sử: " + message, Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void fetchMangaDetails(List<ReadingHistoryResponse> historyList) {
        for (ReadingHistoryResponse item : historyList) {
            int mangaId = item.getMangaId();

            // O1: Check cache first, avoid N+1 API calls
            MangaResponse cached = mangaCache.get(mangaId);
            if (cached != null) {
                item.setMangaTitle(cached.getTitle());
                item.setMangaCoverUrl(cached.getCoverImageUrl());
                item.setMangaSlug(cached.getSlug());
                // B5: Notify by mangaId lookup, not position
                notifyItemByMangaId(mangaId);
                continue;
            }

            // B5: capture mangaId instead of position to avoid race conditions
            mangaRepository.getMangaById(mangaId, new MangaRepository.MangaCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse data) {
                    if (!isAdded()) return;
                    if (data != null) {
                        // Cache for future use
                        mangaCache.put(mangaId, data);
                        item.setMangaTitle(data.getTitle());
                        item.setMangaCoverUrl(data.getCoverImageUrl());
                        item.setMangaSlug(data.getSlug());
                    } else {
                        // O6: Error fallback text
                        item.setMangaTitle("Không tải được thông tin");
                    }
                    notifyItemByMangaId(mangaId);
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    // O6: Error fallback
                    item.setMangaTitle("Không tải được thông tin");
                    notifyItemByMangaId(mangaId);
                }
            });
        }
    }

    // B5: Notify adapter by mangaId instead of position index
    private void notifyItemByMangaId(int mangaId) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            if (!isAdded()) return;
            List<ReadingHistoryResponse> list = adapter.getList();
            if (list == null) return;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMangaId() == mangaId) {
                    adapter.notifyItemChanged(i);
                    return;
                }
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ReadingHistoryResponse item = adapter.getItem(position);
                if (item != null) {
                    showDeleteDialog(item, position);
                }
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvHistory);
    }

    private void showDeleteDialog(ReadingHistoryResponse item, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có chắc muốn xóa lịch sử đọc truyện này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteHistoryItem(item.getMangaId(), position))
                .setNegativeButton("Hủy", (dialog, which) -> adapter.notifyItemChanged(position))
                .setOnCancelListener(dialog -> adapter.notifyItemChanged(position))
                .show();
    }

    private void deleteHistoryItem(int mangaId, int position) {
        userRepository.deleteReadingHistory(mangaId, new UserRepository.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (!isAdded()) return;
                adapter.removeItem(position);
                // Also remove from cache
                mangaCache.remove(mangaId);
                if (adapter.getItemCount() == 0) {
                    showEmptyState();
                }
                Toast.makeText(requireContext(), "Đã xóa lịch sử", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                adapter.notifyItemChanged(position);
                Toast.makeText(requireContext(), "Lỗi xóa lịch sử: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteAllDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa tất cả")
                .setMessage("Bạn có chắc muốn xóa toàn bộ lịch sử đọc?")
                .setPositiveButton("Xóa tất cả", (dialog, which) -> deleteAllHistory())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAllHistory() {
        userRepository.deleteAllReadingHistory(new UserRepository.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (!isAdded()) return;
                adapter.clearList();
                mangaCache.clear();
                showEmptyState();
                Toast.makeText(requireContext(), "Đã xóa toàn bộ lịch sử", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi xóa tất cả: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.GONE);
        btnDeleteAll.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
        rvHistory.setVisibility(View.VISIBLE);
        btnDeleteAll.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onContinueClick(ReadingHistoryResponse item) {
        Intent intent = new Intent(requireContext(), ReaderActivity.class);
        intent.putExtra("MANGA_ID", item.getMangaId());
        intent.putExtra("CHAPTER_ID", item.getChapterId());
        intent.putExtra("LAST_PAGE", item.getLastPage());
        startActivity(intent);
    }

    // B2: Always pass mangaId, include slug if available
    @Override
    public void onItemClick(ReadingHistoryResponse item) {
        if (!isAdded() || item.getMangaId() <= 0) return;

        Bundle args = new Bundle();
        args.putInt("mangaId", item.getMangaId());
        if (item.getMangaSlug() != null && !item.getMangaSlug().isEmpty()) {
            args.putString("mangaSlug", item.getMangaSlug());
        }
        // Dùng Activity NavController vì HistoryFragment nằm trong ViewPager2
        try {
            androidx.navigation.NavController navController =
                    androidx.navigation.Navigation.findNavController(
                            requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_library_to_manga_detail, args);
        } catch (IllegalStateException e) {
            android.util.Log.e("HistoryFragment", "NavController error: " + e.getMessage());
        }
    }
}
