package com.ptithcm.manga.ui.manga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                        adapter.updateData(data.getContent());
                    } else {
                        adapter.updateData(new ArrayList<>());
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
    public void onMangaClick(MangaResponse manga) {
        // Xem chi tiết truyện của tôi
        Bundle bundle = new Bundle();
        bundle.putString("mangaSlug", manga.getSlug());
        Navigation.findNavController(requireView()).navigate(R.id.action_my_mangas_to_manga_detail, bundle);
    }
}
