package com.ptithcm.manga.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.AdminPendingAdapter;
import com.ptithcm.manga.data.model.response.MangaResponse;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

public class AdminPendingFragment extends Fragment implements AdminPendingAdapter.OnPendingActionListener {

    private AdminRepository adminRepository;
    private AdminPendingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_pending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        adminRepository = new AdminRepository(requireContext());
        
        RecyclerView rvPending = view.findViewById(R.id.rv_pending);
        rvPending.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminPendingAdapter(this);
        rvPending.setAdapter(adapter);

        loadPendingMangas();
    }
    
    private void loadPendingMangas() {
        adminRepository.getPendingMangas(0, 50, new AdminRepository.RepositoryCallback<PageResponse<MangaResponse>>() {
            @Override
            public void onSuccess(PageResponse<MangaResponse> result) {
                adapter.setMangas(result.getContent());
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
    public void onApprove(MangaResponse manga, int position) {
        adminRepository.approveManga(manga.getId(), new AdminRepository.RepositoryCallback<MangaResponse>() {
            @Override
            public void onSuccess(MangaResponse result) {
                adapter.removeItem(position);
                Toast.makeText(getContext(), "Đã duyệt truyện thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReject(MangaResponse manga, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Từ chối truyện");
        
        final EditText input = new EditText(requireContext());
        input.setHint("Nhập lý do từ chối");
        builder.setView(input);

        builder.setPositiveButton("Từ chối", (dialog, which) -> {
            String reason = input.getText().toString();
            if (reason.trim().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập lý do!", Toast.LENGTH_SHORT).show();
                return;
            }
            adminRepository.rejectManga(manga.getId(), reason, new AdminRepository.RepositoryCallback<MangaResponse>() {
                @Override
                public void onSuccess(MangaResponse result) {
                    adapter.removeItem(position);
                    Toast.makeText(getContext(), "Đã từ chối truyện!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
