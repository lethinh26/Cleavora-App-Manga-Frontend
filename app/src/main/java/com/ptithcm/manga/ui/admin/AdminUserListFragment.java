package com.ptithcm.manga.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.manga.AdminUserAdapter;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.model.response.UserResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

public class AdminUserListFragment extends Fragment implements AdminUserAdapter.OnUserActionListener {

    private AdminRepository adminRepository;
    private AdminUserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        adminRepository = new AdminRepository(requireContext());
        
        RecyclerView rvUsers = view.findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminUserAdapter(this);
        rvUsers.setAdapter(adapter);

        loadUsers();
    }
    
    private void loadUsers() {
        adminRepository.getAllUsers(0, 50, new AdminRepository.RepositoryCallback<PageResponse<UserResponse>>() {
            @Override
            public void onSuccess(PageResponse<UserResponse> result) {
                adapter.setUsers(result.getContent());
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
    public void onUserClick(UserResponse user, int position) {
        String[] options = {"Khóa / Mở khóa tài khoản", "Đổi Role (Chỉ SuperAdmin)"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Hành động cho " + user.getEmail());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                toggleUserActive(user, position);
            } else if (which == 1) {
                showChangeRoleDialog(user, position);
            }
        });
        builder.show();
    }
    
    private void toggleUserActive(UserResponse user, int position) {
        adminRepository.toggleUserActive(user.getId(), new AdminRepository.RepositoryCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse result) {
                adapter.updateItem(position, result);
                Toast.makeText(getContext(), "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showChangeRoleDialog(UserResponse user, int position) {
        String[] roles = {"USER", "ADMIN", "SUPERADMIN"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn Role mới");
        builder.setItems(roles, (dialog, which) -> {
            String selectedRole = roles[which];
            adminRepository.changeUserRole(user.getId(), selectedRole, new AdminRepository.RepositoryCallback<UserResponse>() {
                @Override
                public void onSuccess(UserResponse result) {
                    adapter.updateItem(position, result);
                    Toast.makeText(getContext(), "Đổi Role thành công", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.show();
    }
}
