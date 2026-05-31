package com.ptithcm.manga.ui.admin;

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

import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.response.DashboardStatsResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

public class AdminDashboardFragment extends Fragment {

    private AdminRepository adminRepository;
    private TextView tvStat1, tvStat2, tvStat3, tvStat1Label, tvStat2Label, tvStat3Label;

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

        // Find Stat Cards
        View card1 = ((ViewGroup) view.findViewById(R.id.btn_pending_manga).getParent().getParent()).getChildAt(1);
        if (card1 instanceof ViewGroup) {
            ViewGroup statsContainer = (ViewGroup) card1;
            
            View stat1 = statsContainer.getChildAt(0);
            tvStat1 = stat1.findViewById(R.id.tv_stat_value);
            tvStat1Label = stat1.findViewById(R.id.tv_stat_label);
            tvStat1Label.setText("Người dùng");
            
            View stat2 = statsContainer.getChildAt(1);
            tvStat2 = stat2.findViewById(R.id.tv_stat_value);
            tvStat2Label = stat2.findViewById(R.id.tv_stat_label);
            tvStat2Label.setText("Truyện");
            
            View stat3 = statsContainer.getChildAt(2);
            tvStat3 = stat3.findViewById(R.id.tv_stat_value);
            tvStat3Label = stat3.findViewById(R.id.tv_stat_label);
            tvStat3Label.setText("Chương");
        }

        // Setup menu buttons
        view.findViewById(R.id.btn_pending_manga).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_admin_to_pending));

        view.findViewById(R.id.btn_manage_manga).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_admin_to_manga_list));

        view.findViewById(R.id.btn_manage_genre).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_admin_to_genre));

        view.findViewById(R.id.btn_manage_user).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_admin_to_user_list));

        loadStats();
    }
    
    private void loadStats() {
        adminRepository.getDashboardStats(new AdminRepository.RepositoryCallback<DashboardStatsResponse>() {
            @Override
            public void onSuccess(DashboardStatsResponse result) {
                if (tvStat1 != null) {
                    tvStat1.setText(String.valueOf(result.getTotalUsers()));
                    tvStat2.setText(String.valueOf(result.getTotalMangas()));
                    tvStat3.setText(String.valueOf(result.getTotalChapters()));
                }
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
