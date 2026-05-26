package com.ptithcm.manga.ui.manga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

public class MangaDetailFragment extends Fragment {

    private int mangaId;

    private MaterialButton btnLike;
    private TextView tvLikes;

    private MangaRepository mangaRepository;
    private TokenManager tokenManager;

    private boolean isLiked = false;
    private int likeCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mangaId = MangaDetailFragmentArgs.fromBundle(getArguments()).getMangaId();

        btnLike = view.findViewById(R.id.btn_like);
        tvLikes = view.findViewById(R.id.tv_likes);

        mangaRepository = new MangaRepository(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());

        if (!tokenManager.isLoggedIn()) {
            btnLike.setEnabled(false);
            btnLike.setText("Đăng nhập để thích");
            return;
        }

        loadLikeStatus();

        btnLike.setOnClickListener(v -> toggleLike());
    }

    private void loadLikeStatus() {
        mangaRepository.getLikeStatus(mangaId, new MangaRepository.MangaCallback<LikeStatusResponse>() {
            @Override
            public void onSuccess(LikeStatusResponse data) {
                isLiked = data.isLiked();
                updateLikeButton();
            }

            @Override
            public void onError(String message) {
                // Silently fail — button stays in default state
            }
        });
    }

    private void toggleLike() {
        btnLike.setEnabled(false);

        mangaRepository.toggleLike(mangaId, new MangaRepository.MangaCallback<LikeResponse>() {
            @Override
            public void onSuccess(LikeResponse data) {
                isLiked = data.isLiked();
                likeCount = data.getLikeCount();
                updateLikeButton();
                tvLikes.setText(String.valueOf(likeCount));
                btnLike.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                btnLike.setEnabled(true);
            }
        });
    }

    private void updateLikeButton() {
        if (isLiked) {
            btnLike.setIconResource(android.R.drawable.btn_star_big_on);
            btnLike.setText("Đã thích");
        } else {
            btnLike.setIconResource(android.R.drawable.btn_star_big_off);
            btnLike.setText("Yêu thích");
        }
    }
}
