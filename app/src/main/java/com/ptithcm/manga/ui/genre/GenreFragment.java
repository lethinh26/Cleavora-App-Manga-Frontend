package com.ptithcm.manga.ui.genre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.genre.GenreChipAdapter;
import com.ptithcm.manga.adapter.manga.MangaCardAdapter;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends Fragment implements MangaCardAdapter.OnMangaClickListener {

    private RecyclerView rvGenres;
    private RecyclerView rvMangasByGenre;
    private TextView tvSelectedGenreTitle;

    private GenreChipAdapter genreAdapter;
    private MangaCardAdapter mangaAdapter;
    private MangaRepository mangaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());

        // Initialize views
        rvGenres = view.findViewById(R.id.rv_genres);
        rvMangasByGenre = view.findViewById(R.id.rv_mangas_by_genre);
        tvSelectedGenreTitle = view.findViewById(R.id.tv_selected_genre_title);

        // Setup Genres Grid
        genreAdapter = new GenreChipAdapter();
        rvGenres.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvGenres.setAdapter(genreAdapter);

        // Setup Manga Horizontal List
        mangaAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvMangasByGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMangasByGenre.setAdapter(mangaAdapter);

        genreAdapter.setOnGenreClickListener(genre -> {
            loadMangaByGenre(genre.getSlug(), genre.getName());
        });

        loadGenres();
    }

    private void loadGenres() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (data != null) {
                        genreAdapter.setGenres(data);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Lỗi tải thể loại: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadMangaByGenre(String slug, String genreName) {
        mangaRepository.getMangasByGenre(slug, new MangaRepository.MangaCallback<List<MangaResponse>>() {
            @Override
            public void onSuccess(List<MangaResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvSelectedGenreTitle.setText("Truyện thể loại: " + genreName);
                    tvSelectedGenreTitle.setVisibility(View.VISIBLE);
                    rvMangasByGenre.setVisibility(View.VISIBLE);

                    mangaAdapter.updateData(data);

                    if (data.isEmpty()) {
                        Toast.makeText(getContext(), "Không có truyện nào thuộc thể loại này", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    String error = (message != null) ? message : "Lỗi tải danh sách truyện";
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onMangaClick(MangaResponse manga) {
        Bundle bundle = new Bundle();
        bundle.putInt("mangaId", manga.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_genre_to_manga_detail, bundle);
    }
}
