package com.ptithcm.manga.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.MangaCardAdapter;
import com.ptithcm.manga.data.model.response.GenreResponse;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MangaCardAdapter.OnMangaClickListener {
    private MangaRepository mangaRepository;
    private RecyclerView rvLatest, rvPopular, rvCompleted;
    private TextView tvLatestHeader, tvPopularHeader, tvCompletedHeader;
    private View bannerContainer;
    private Spinner spinnerGenres;
    
    private MangaCardAdapter latestAdapter, popularAdapter, completedAdapter;
    private List<GenreResponse> fullGenreList = new ArrayList<>();
    private boolean isFirstSelection = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());
        
        // Bind views
        rvLatest = view.findViewById(R.id.rv_latest);
        rvPopular = view.findViewById(R.id.rv_popular);
        rvCompleted = view.findViewById(R.id.rv_completed);
        tvLatestHeader = view.findViewById(R.id.tv_latest_header);
        tvPopularHeader = view.findViewById(R.id.tv_popular_header);
        tvCompletedHeader = view.findViewById(R.id.tv_completed_header);
        bannerContainer = view.findViewById(R.id.banner_container);
        spinnerGenres = view.findViewById(R.id.spinner_genres);
        
        setupRecyclerViews();
        setupGenreSpinner();
        loadingDefaultData();
    }

    private void setupRecyclerViews() {
        latestAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvLatest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvLatest.setAdapter(latestAdapter);

        popularAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopular.setAdapter(popularAdapter);

        completedAdapter = new MangaCardAdapter(new ArrayList<>(), this);
        rvCompleted.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCompleted.setAdapter(completedAdapter);
    }

    private void setupGenreSpinner() {
        mangaRepository.getGenres(new MangaRepository.MangaCallback<List<GenreResponse>>() {
            @Override
            public void onSuccess(List<GenreResponse> data) {
                if (!isAdded() || data == null) return;
                fullGenreList = data;
                List<String> genreNames = new ArrayList<>();
                genreNames.add("Tất cả thể loại");
                for (GenreResponse genre : data) {
                    genreNames.add(genre.getName());
                }

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, genreNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenres.setAdapter(adapter);

                    spinnerGenres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (isFirstSelection) {
                                isFirstSelection = false;
                                return;
                            }
                            
                            if (position == 0) {
                                loadingDefaultData();
                            } else if (position - 1 < fullGenreList.size()) {
                                GenreResponse selected = fullGenreList.get(position - 1);
                                filterByGenre(selected.getSlug(), selected.getName());
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                });
            }

            @Override
            public void onError(String message) {
                showError(message);
            }
        });
    }

    private void loadingDefaultData() {
        mangaRepository.getMangas(new MangaRepository.MangaCallback<List<MangaResponse>>() {
            @Override
            public void onSuccess(List<MangaResponse> data) {
                if (!isAdded() || data == null) return;
                requireActivity().runOnUiThread(() -> {
                    tvLatestHeader.setText("Mới cập nhật");
                    setOtherSectionsVisibility(View.VISIBLE);
                    
                    latestAdapter.updateData(data);
                    
                    List<MangaResponse> completed = new ArrayList<>();
                    for (MangaResponse m : data) {
                        if (m.getStatus() == MangaResponse.MangaStatus.COMPLETED) {
                            completed.add(m);
                        }
                    }
                    completedAdapter.updateData(completed);
                    popularAdapter.updateData(data); 
                });
            }
            @Override
            public void onError(String message) { showError(message); }
        });
    }

    private void filterByGenre(String slug, String genreName) {
        mangaRepository.getMangasByGenre(slug, new MangaRepository.MangaCallback<List<MangaResponse>>() {
            @Override
            public void onSuccess(List<MangaResponse> data) {
                if (!isAdded() || data == null) return;
                requireActivity().runOnUiThread(() -> {
                    tvLatestHeader.setText("Kết quả: " + genreName);
                    latestAdapter.updateData(data);
                    setOtherSectionsVisibility(View.GONE);
                });
            }
            @Override
            public void onError(String message) { showError(message); }
        });
    }

    private void setOtherSectionsVisibility(int visibility) {
        if (rvPopular != null) rvPopular.setVisibility(visibility);
        if (tvPopularHeader != null) tvPopularHeader.setVisibility(visibility);
        if (rvCompleted != null) rvCompleted.setVisibility(visibility);
        if (tvCompletedHeader != null) tvCompletedHeader.setVisibility(visibility);
        if (bannerContainer != null) bannerContainer.setVisibility(visibility);
    }

    private void showError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            String msg = (message != null) ? message : "Lỗi kết nối";
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMangaClick(MangaResponse manga) {
        Bundle bundle = new Bundle();
        bundle.putString("mangaSlug", manga.getSlug());
        Navigation.findNavController(requireView()).navigate(R.id.action_home_to_manga_detail, bundle);
    }
}
