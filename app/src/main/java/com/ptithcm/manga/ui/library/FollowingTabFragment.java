package com.ptithcm.manga.ui.library;

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
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.FollowListResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

public class FollowingTabFragment extends Fragment {

    private RecyclerView rvFollows;
    private TextView tvEmpty;

    private MangaRepository mangaRepository;
    private MangaCardAdapter adapter;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_tab, container, false);
        rvFollows = view.findViewById(R.id.rv_favorites);
        tvEmpty = view.findViewById(R.id.tv_empty);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaRepository = new MangaRepository(requireContext());

        adapter = new MangaCardAdapter(manga -> {
            Bundle args = new Bundle();
            args.putString("mangaSlug", manga.getSlug());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_library_to_manga_detail, args);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        rvFollows.setLayoutManager(layoutManager);
        rvFollows.setAdapter(adapter);

        rvFollows.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();

                    if (visibleItemCount + firstVisiblePosition >= totalItemCount) {
                        loadFollows();
                    }
                }
            }
        });

        loadFollows();
    }

    private void loadFollows() {
        isLoading = true;
        mangaRepository.getFollows(currentPage, PAGE_SIZE, new MangaRepository.MangaCallback<FollowListResponse>() {
            @Override
            public void onSuccess(FollowListResponse data) {
                adapter.addMangas(data.getContent());
                isLastPage = data.isLast();
                currentPage++;
                isLoading = false;

                if (adapter.getItemCount() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvFollows.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvFollows.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                isLoading = false;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
