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
import com.ptithcm.manga.data.model.response.FollowResponse;
import com.ptithcm.manga.data.model.response.FollowStatusResponse;
import com.ptithcm.manga.data.model.response.LikeResponse;
import com.ptithcm.manga.data.model.response.LikeStatusResponse;
import com.ptithcm.manga.data.repository.MangaRepository;

public class MangaDetailFragment extends Fragment {

    private int mangaId;

    private MaterialButton btnLike;
    private MaterialButton btnFollow;
    private TextView tvLikes;
    private TextView tvFollows;

    private MangaRepository mangaRepository;
    private TokenManager tokenManager;

    private boolean isLiked = false;
    private boolean isFollowed = false;
    private int likeCount = 0;
    private int followCount = 0;

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
        btnFollow = view.findViewById(R.id.btn_follow);
        tvLikes = view.findViewById(R.id.tv_likes);
        tvFollows = view.findViewById(R.id.tv_follows);

        mangaRepository = new MangaRepository(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());

        if (!tokenManager.isLoggedIn()) {
            btnLike.setEnabled(false);
            btnLike.setText("Đăng nhập để thích");
            btnFollow.setEnabled(false);
            btnFollow.setText("Đăng nhập để theo dõi");
            return;
        }

        loadLikeStatus();
        loadFollowStatus();

        btnLike.setOnClickListener(v -> toggleLike());
        btnFollow.setOnClickListener(v -> toggleFollow());
    }

    // ============ LIKE ============

    private void loadLikeStatus() {
        mangaRepository.getLikeStatus(mangaId, new MangaRepository.MangaCallback<LikeStatusResponse>() {
            @Override
            public void onSuccess(LikeStatusResponse data) {
                isLiked = data.isLiked();
                updateLikeButton();
            }

            @Override
            public void onError(String message) {
                // silently fail
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

    // ============ FOLLOW ============

    private void loadFollowStatus() {
        mangaRepository.getFollowStatus(mangaId, new MangaRepository.MangaCallback<FollowStatusResponse>() {
            @Override
            public void onSuccess(FollowStatusResponse data) {
                isFollowed = data.isFollowed();
                updateFollowButton();
            }

            @Override
            public void onError(String message) {
                // silently fail
            }
        });
    }

    private void toggleFollow() {
        btnFollow.setEnabled(false);
        mangaRepository.toggleFollow(mangaId, new MangaRepository.MangaCallback<FollowResponse>() {
            @Override
            public void onSuccess(FollowResponse data) {
                isFollowed = data.isFollowed();
                followCount = data.getFollowCount();
                updateFollowButton();
                tvFollows.setText(String.valueOf(followCount));
                btnFollow.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                btnFollow.setEnabled(true);
            }
        });
    }

    private void updateFollowButton() {
        if (isFollowed) {
            btnFollow.setIconResource(android.R.drawable.btn_star_big_on);
            btnFollow.setText("Đang theo dõi");
        } else {
            btnFollow.setIconResource(android.R.drawable.btn_star_big_off);
            btnFollow.setText("Theo dõi");
        }
    }
}
