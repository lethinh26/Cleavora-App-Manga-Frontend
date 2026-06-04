package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.admin.GenreManageAdapter;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.repository.AdminRepository;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminGenreFragment extends Fragment implements GenreManageAdapter.GenreManageListener {

    private AdminRepository adminRepository;
    private MangaRepository mangaRepository;
    private RecyclerView rvGenres;
    private TextInputEditText etGenreName;
    private View btnAddGenre;
    private SwipeRefreshLayout swipeRefresh;
    private GenreManageAdapter adapter;

    private final List<GenreResponse> genreList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminRepository = new AdminRepository(requireContext());
        mangaRepository = new MangaRepository(requireContext());

        rvGenres = view.findViewById(R.id.rv_genres);
        etGenreName = view.findViewById(R.id.et_genre_name);
        btnAddGenre = view.findViewById(R.id.btn_add_genre);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        adapter = new GenreManageAdapter(this);
        rvGenres.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGenres.setAdapter(adapter);

        btnAddGenre.setOnClickListener(v -> {
            String name = etGenreName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
                return;
            }
            createGenre(name);
        });

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::loadGenres);
        }

        loadGenres();
    }

    private void loadGenres() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    genreList.clear();
                    genreList.addAll(data);
                    adapter.setItems(genreList);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }
        });
    }

    private String toSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    private void createGenre(String name) {
        String slug = toSlug(name);
        adminRepository.createGenre(name, slug, new AdminRepository.AdminCallback<GenreResponse>() {
            @Override
            public void onSuccess(GenreResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    etGenreName.setText("");
                    Toast.makeText(requireContext(), "Đã thêm thể loại", Toast.LENGTH_SHORT).show();
                    loadGenres();
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onEdit(GenreResponse genre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sửa thể loại");

        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(genre.getName());
        input.setPadding(32, 16, 32, 16);
        builder.setView(input);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            adminRepository.updateGenre(genre.getId(), newName, toSlug(newName), new AdminRepository.AdminCallback<GenreResponse>() {
                @Override
                public void onSuccess(GenreResponse data) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Đã cập nhật thể loại", Toast.LENGTH_SHORT).show();
                        loadGenres();
                    });
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                }
            });
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onDelete(GenreResponse genre) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa thể loại")
                .setMessage("Xóa thể loại \"" + genre.getName() + "\"? Các truyện thuộc thể loại này sẽ bị gỡ liên kết.")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    adminRepository.deleteGenre(genre.getId(), new AdminRepository.AdminCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Đã xóa thể loại", Toast.LENGTH_SHORT).show();
                                loadGenres();
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
