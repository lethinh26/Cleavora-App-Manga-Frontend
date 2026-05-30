package com.ptithcm.manga.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.AdminMangaAdapter;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

public class AdminMangaListFragment extends Fragment implements AdminMangaAdapter.OnMangaActionListener {

    private AdminRepository mangaRepository;
    private AdminMangaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_manga_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mangaRepository = new AdminRepository(requireContext());
        
        RecyclerView rvMangas = view.findViewById(R.id.rv_mangas);
        rvMangas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminMangaAdapter(this);
        rvMangas.setAdapter(adapter);

        view.findViewById(R.id.btn_add_manga).setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_admin_manga_to_form));

        loadMangas();
    }
    
    private void loadMangas() {
        mangaRepository.getAllMangas(0, 100, null, new AdminRepository.RepositoryCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> result) {
                adapter.setMangas(result.getContent());
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onEdit(MangaResponse manga, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("mangaId", manga.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_admin_manga_to_form, bundle);
    }

    @Override
    public void onDelete(MangaResponse manga, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa truyện")
                .setMessage("Bạn có chắc chắn muốn xóa truyện này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    mangaRepository.deleteManga(manga.getId(), new AdminRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            adapter.removeManga(position);
                            Toast.makeText(getContext(), "Đã xóa truyện", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onAddChapter(MangaResponse manga, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("mangaId", manga.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_admin_manga_to_chapter_form, bundle);
    }
}
