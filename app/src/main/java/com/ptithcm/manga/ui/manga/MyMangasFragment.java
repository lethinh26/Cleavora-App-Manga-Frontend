package com.ptithcm.manga.ui.manga;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.MangaCardAdapter;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class MyMangasFragment extends Fragment implements MangaCardAdapter.OnMangaClickListener {

    private MangaRepository mangaRepository;
    private RecyclerView rvMyMangas;
    private MangaCardAdapter adapter;
    private TabLayout tabLayout;
    private MaterialButton btnSubmitNew;

    private String currentStatus = null; // null tương ứng với "Tất cả"
    private List<MangaResponse> currentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_mangas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());

        // Khởi tạo các view
        rvMyMangas = view.findViewById(R.id.rv_my_mangas);
        tabLayout = view.findViewById(R.id.tab_layout);
        btnSubmitNew = view.findViewById(R.id.btn_submit_new);

        setupRecyclerView();
        setupTabs();
        setupListeners();

        // Tải dữ liệu lần đầu
        loadMyMangas();
    }

    private void setupRecyclerView() {
        adapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvMyMangas.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMyMangas.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentStatus = null; break; // Tất cả
                    case 1: currentStatus = "PENDING"; break; // Chờ duyệt
                    case 2: currentStatus = "APPROVED"; break; // Đã duyệt
                    case 3: currentStatus = "REJECTED"; break; // Bị từ chối
                }
                loadMyMangas();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupListeners() {
        btnSubmitNew.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_my_mangas_to_submit));
    }

    private void loadMyMangas() {
        mangaRepository.getMyMangas(currentStatus, 0, 50, new MangaRepository.MangaCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (data != null && data.getContent() != null) {
                        currentList = data.getContent();
                        adapter.updateData(currentList);
                    } else {
                        currentList = new ArrayList<>();
                        adapter.updateData(currentList);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), message != null ? message : "Lỗi tải danh sách truyện", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onMangaClick(MangaResponse manga, View itemView) {
        // Show popup menu anchored to the clicked item
        PopupMenu popup = new PopupMenu(requireContext(), itemView);

        // Chỉ hiện "Xem chi tiết" khi truyện đã được duyệt hoặc bị từ chối (có dữ liệu)
        if (manga.getApprovalStatus() != MangaResponse.ApprovalStatus.PENDING) {
            popup.getMenu().add(0, 1, 0, "Xem chi tiết");
        }

        // Only show "Quản lý chương" for APPROVED mangas
        if (manga.getApprovalStatus() == MangaResponse.ApprovalStatus.APPROVED) {
            popup.getMenu().add(0, 2, 1, "Quản lý chương");
        }

        // Hiện "Chỉnh sửa truyện" cho các trạng thái không bị BANNED
        if (manga.getApprovalStatus() != MangaResponse.ApprovalStatus.BANNED) {
            popup.getMenu().add(0, 4, 2, "Chỉnh sửa truyện");
        }

        popup.getMenu().add(0, 3, 3, "Xoá truyện");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1: // View detail
                    Bundle bundle = new Bundle();
                    bundle.putString("mangaSlug", manga.getSlug());
                    Navigation.findNavController(requireView()).navigate(R.id.action_my_mangas_to_manga_detail, bundle);
                    return true;
                case 2: // Manage chapters
                    Bundle args = new Bundle();
                    args.putInt("mangaId", manga.getId());
                    Navigation.findNavController(requireView()).navigate(R.id.action_my_mangas_to_chapter_form, args);
                    return true;
                case 4: // Edit manga
                    Bundle editArgs = new Bundle();
                    editArgs.putInt("mangaId", manga.getId());
                    editArgs.putString("title", manga.getTitle());
                    editArgs.putString("author", manga.getAuthorName());
                    editArgs.putString("description", manga.getDescription());
                    editArgs.putString("coverUrl", manga.getCoverImageUrl());
                    editArgs.putString("status", manga.getStatus() != null
                            ? manga.getStatus().name() : "ONGOING");
                    Navigation.findNavController(requireView()).navigate(R.id.action_my_mangas_to_edit_manga, editArgs);
                    return true;
                case 3: // Delete manga
                    confirmDeleteManga(manga.getId());
                    return true;
            }
            return false;
        });
        popup.show();
    }

    private void confirmDeleteManga(int mangaId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá truyện")
                .setMessage("Bạn có chắc muốn xoá truyện này? Toàn bộ chapters sẽ bị xoá.")
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    mangaRepository.deleteMyManga(mangaId, new MangaRepository.MangaCallback<Object>() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Đã xoá truyện", Toast.LENGTH_SHORT).show();
                                loadMyMangas();
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
}
