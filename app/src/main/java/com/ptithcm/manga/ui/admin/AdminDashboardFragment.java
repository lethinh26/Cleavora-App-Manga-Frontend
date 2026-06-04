package com.ptithcm.manga.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.DashboardStatsResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

public class AdminDashboardFragment extends Fragment {

    private AdminRepository adminRepository;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;

    private TextView tvStatUsers, tvStatMangas, tvStatChapters, tvStatPending;
    private TextView tvStatUsersLabel, tvStatMangasLabel, tvStatChaptersLabel, tvStatPendingLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminRepository = new AdminRepository(requireContext());

        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);

        // Stat cards
        View statUsers = view.findViewById(R.id.stat_users);
        View statMangas = view.findViewById(R.id.stat_mangas);
        View statChapters = view.findViewById(R.id.stat_chapters);
        View statPending = view.findViewById(R.id.stat_pending);

        tvStatUsers = statUsers.findViewById(R.id.tv_stat_value);
        tvStatUsersLabel = statUsers.findViewById(R.id.tv_stat_label);
        tvStatMangas = statMangas.findViewById(R.id.tv_stat_value);
        tvStatMangasLabel = statMangas.findViewById(R.id.tv_stat_label);
        tvStatChapters = statChapters.findViewById(R.id.tv_stat_value);
        tvStatChaptersLabel = statChapters.findViewById(R.id.tv_stat_label);
        tvStatPending = statPending.findViewById(R.id.tv_stat_value);
        tvStatPendingLabel = statPending.findViewById(R.id.tv_stat_label);

        tvStatUsersLabel.setText(R.string.total_user);
        tvStatMangasLabel.setText(R.string.total_manga);
        tvStatChaptersLabel.setText(R.string.total_chapter);
        tvStatPendingLabel.setText(R.string.pending_mangas);

        // Menu navigation
        view.findViewById(R.id.btn_pending_manga).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_admin_to_pending));
        view.findViewById(R.id.btn_manage_manga).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_admin_to_manga_list));
        view.findViewById(R.id.btn_manage_genre).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_admin_to_genre));
        view.findViewById(R.id.btn_manage_user).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_admin_to_user_list));

        // Swipe to refresh
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::loadStats);
        }

        loadStats();
    }

    private void loadStats() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        adminRepository.getDashboardStats(new AdminRepository.AdminCallback<DashboardStatsResponse>() {
            @Override
            public void onSuccess(DashboardStatsResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvStatUsers.setText(String.valueOf(data.getTotalUsers()));
                    tvStatMangas.setText(String.valueOf(data.getTotalMangas()));
                    tvStatChapters.setText(String.valueOf(data.getTotalChapters()));
                    tvStatPending.setText(String.valueOf(data.getTotalPendingMangas()));

                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }
        });
    }
}
