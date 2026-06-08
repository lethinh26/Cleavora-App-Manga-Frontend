package com.ptithcm.manga.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.MangaCardAdapter;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements MangaCardAdapter.OnMangaClickListener {

    private EditText etSearch;
    private ImageButton btnSearchSubmit;
    private RecyclerView rvResults;
    private MangaCardAdapter mangaAdapter;
    private MangaRepository mangaRepository;
    private List<MangaResponse> mangaList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mangaRepository = new MangaRepository(requireContext());
        
        etSearch = view.findViewById(R.id.et_search);
        btnSearchSubmit = view.findViewById(R.id.btn_search_submit);
        rvResults = view.findViewById(R.id.rv_search_results);

        setupRecyclerView();
        setupSearchListeners();
    }

    private void setupRecyclerView() {
        mangaAdapter = new MangaCardAdapter(mangaList, this);
        rvResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvResults.setAdapter(mangaAdapter);
    }

    private void setupSearchListeners() {
        // Submit via Button
        btnSearchSubmit.setOnClickListener(v -> handleSearch());

        // Submit via Keyboard Search icon
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearch();
                return true;
            }
            return false;
        });
    }

    private void handleSearch() {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            hideKeyboard();
            performSearch(query);
        } else {
            Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        etSearch.clearFocus();
    }

    private void performSearch(String keyword) {
        mangaRepository.searchManga(keyword, 0, 20, new MangaRepository.MangaCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (data != null && data.getContent() != null) {
                        mangaAdapter.updateData(data.getContent());
                        if (data.getContent().isEmpty()) {
                            Toast.makeText(getContext(), "Không tìm thấy kết quả phù hợp", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    String error = (message != null) ? message : "Lỗi tìm kiếm";
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onMangaClick(MangaResponse manga, View itemView) {
        Bundle bundle = new Bundle();
        bundle.putString("mangaSlug", manga.getSlug());
        Navigation.findNavController(requireView()).navigate(R.id.action_search_to_manga_detail, bundle);
    }
}
