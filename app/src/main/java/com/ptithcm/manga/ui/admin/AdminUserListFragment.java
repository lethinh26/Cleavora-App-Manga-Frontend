package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ptithcm.manga.R;
import com.ptithcm.manga.adapter.admin.UserListAdapter;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.PageResponse;
import com.ptithcm.manga.data.model.response.UserResponse;
import com.ptithcm.manga.data.repository.AdminRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListFragment extends Fragment implements UserListAdapter.UserListListener {

    private AdminRepository adminRepository;
    private RecyclerView rvUsers;
    private SwipeRefreshLayout swipeRefresh;
    private UserListAdapter adapter;

    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLoading = false;
    private final List<UserResponse> userList = new ArrayList<>();

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
        rvUsers = view.findViewById(R.id.rv_users);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        boolean isSuperAdmin = "SUPERADMIN".equals(TokenManager.getInstance(requireContext()).getRole());
        adapter = new UserListAdapter(this, isSuperAdmin);
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUsers.setAdapter(adapter);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                currentPage = 0;
                loadUsers();
            });
        }

        rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && dy > 0 && currentPage < totalPages - 1) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (lm != null && lm.findLastVisibleItemPosition() >= adapter.getItemCount() - 3) {
                        loadMore();
                    }
                }
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        isLoading = true;
        currentPage = 0;
        userList.clear();

        adminRepository.getUsers(0, 20, new AdminRepository.AdminCallback<PageResponse<UserResponse>>() {
            @Override
            public void onSuccess(PageResponse<UserResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    userList.addAll(data.getContent());
                    totalPages = data.getTotalPages();
                    adapter.setItems(userList);
                    isLoading = false;
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        int nextPage = currentPage + 1;

        adminRepository.getUsers(nextPage, 20, new AdminRepository.AdminCallback<PageResponse<UserResponse>>() {
            @Override
            public void onSuccess(PageResponse<UserResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    userList.addAll(data.getContent());
                    currentPage = nextPage;
                    adapter.setItems(userList);
                    isLoading = false;
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }
        });
    }

    @Override
    public void onToggleActive(int userId) {
        adminRepository.toggleUserActive(userId, new AdminRepository.AdminCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    adapter.updateItem(data);
                    Toast.makeText(requireContext(), "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onChangeRole(int userId) {
        String[] roles = {"USER", "ADMIN"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn role mới")
                .setItems(roles, (dialog, which) -> {
                    String newRole = roles[which];
                    adminRepository.changeUserRole(userId, newRole, new AdminRepository.AdminCallback<UserResponse>() {
                        @Override
                        public void onSuccess(UserResponse data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                adapter.updateItem(data);
                                Toast.makeText(requireContext(), "Đã đổi role thành " + newRole, Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                        }
                    });
                })
                .show();
    }
}
