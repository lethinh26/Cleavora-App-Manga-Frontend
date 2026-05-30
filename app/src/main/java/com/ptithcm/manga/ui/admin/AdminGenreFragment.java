package com.ptithcm.manga.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.AdminGenreAdapter;
import com.ptithcm.manga.data.model.Genre;
import com.ptithcm.manga.data.repository.GenreRepository;

import java.util.List;

public class AdminGenreFragment extends Fragment implements AdminGenreAdapter.OnGenreActionListener {

    private GenreRepository genreRepository;
    private AdminGenreAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        genreRepository = new GenreRepository(requireContext());
        
        RecyclerView rvGenres = view.findViewById(R.id.rv_genres);
        rvGenres.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminGenreAdapter(this);
        rvGenres.setAdapter(adapter);

        view.findViewById(R.id.btn_add_genre).setOnClickListener(v -> showGenreDialog(null, -1));

        loadGenres();
    }
    
    private void loadGenres() {
        genreRepository.getAllGenres(new GenreRepository.RepositoryCallback<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> result) {
                adapter.setGenres(result);
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
    public void onEdit(Genre genre, int position) {
        showGenreDialog(genre, position);
    }

    @Override
    public void onDelete(Genre genre, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa thể loại")
                .setMessage("Bạn có chắc chắn muốn xóa thể loại này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    genreRepository.deleteGenre(genre.getId(), new GenreRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            adapter.removeGenre(position);
                            Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
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

    private void showGenreDialog(@Nullable Genre genre, int position) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_genre, null);
        EditText etName = view.findViewById(R.id.et_name);
        EditText etSlug = view.findViewById(R.id.et_slug);

        if (genre != null) {
            etName.setText(genre.getName());
            etSlug.setText(genre.getSlug());
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(genre == null ? "Thêm thể loại mới" : "Sửa thể loại")
                .setView(view)
                .setPositiveButton("Lưu", null) // Set null to prevent auto-dismiss
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSave.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String slug = etSlug.getText().toString().trim();

                if (name.isEmpty() || slug.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                    return;
                }

                Genre newGenre = new Genre();
                newGenre.setName(name);
                newGenre.setSlug(slug);

                if (genre == null) {
                    // Create
                    genreRepository.createGenre(newGenre, new GenreRepository.RepositoryCallback<Genre>() {
                        @Override
                        public void onSuccess(Genre result) {
                            adapter.addGenre(result);
                            Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Update
                    genreRepository.updateGenre(genre.getId(), newGenre, new GenreRepository.RepositoryCallback<Genre>() {
                        @Override
                        public void onSuccess(Genre result) {
                            adapter.updateGenre(position, result);
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        dialog.show();
    }
}
