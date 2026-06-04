package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
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
import java.util.stream.Collectors;

public class AdminUserListFragment extends Fragment implements UserListAdapter.UserListListener {

    private AdminRepository adminRepository;
    private RecyclerView rvUsers;
    private SwipeRefreshLayout swipeRefresh;
    private UserListAdapter adapter;
    private TextView tvUserCount;
    private View layoutEmpty;
    private ProgressBar progressBar;

    // Filter chips
    private TextView chipAll, chipActive, chipInactive;
    private String currentFilter = "ALL"; // ALL | ACTIVE | INACTIVE

    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLoading = false;
    private final List<UserResponse> allUsers = new ArrayList<>();
    private final List<UserResponse> filteredUsers = new ArrayList<>();

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
        tvUserCount = view.findViewById(R.id.tv_user_count);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        progressBar = view.findViewById(R.id.progress_bar);
        chipAll = view.findViewById(R.id.chip_all);
        chipActive = view.findViewById(R.id.chip_active);
        chipInactive = view.findViewById(R.id.chip_inactive);

        boolean isSuperAdmin = "SUPERADMIN".equals(TokenManager.getInstance(requireContext()).getRole());
        adapter = new UserListAdapter(this, isSuperAdmin);
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUsers.setAdapter(adapter);

        // Filter chips click
        chipAll.setOnClickListener(v -> applyFilter("ALL"));
        chipActive.setOnClickListener(v -> applyFilter("ACTIVE"));
        chipInactive.setOnClickListener(v -> applyFilter("INACTIVE"));

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.primary);
            swipeRefresh.setBackgroundResource(R.color.surface);
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

        showLoading(true);
        loadUsers();
    }

    private void applyFilter(String filter) {
        currentFilter = filter;

        // Update chip visual state
        setChipActive(chipAll, "ALL".equals(filter));
        setChipActive(chipActive, "ACTIVE".equals(filter));
        setChipActive(chipInactive, "INACTIVE".equals(filter));

        // Filter list
        filteredUsers.clear();
        if ("ACTIVE".equals(filter)) {
            for (UserResponse u : allUsers) {
                if (Boolean.TRUE.equals(u.getActive())) filteredUsers.add(u);
            }
        } else if ("INACTIVE".equals(filter)) {
            for (UserResponse u : allUsers) {
                if (!Boolean.TRUE.equals(u.getActive())) filteredUsers.add(u);
            }
        } else {
            filteredUsers.addAll(allUsers);
        }

        adapter.setItems(filteredUsers);
        updateEmptyState();
        updateCountLabel();
    }

    private void setChipActive(TextView chip, boolean active) {
        if (active) {
            chip.setBackgroundResource(R.drawable.bg_chip_active);
            chip.setTextColor(requireContext().getColor(R.color.on_primary));
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip);
            chip.setTextColor(requireContext().getColor(R.color.subtext));
        }
    }

    private void loadUsers() {
        isLoading = true;
        currentPage = 0;
        allUsers.clear();

        adminRepository.getUsers(0, 20, new AdminRepository.AdminCallback<PageResponse<UserResponse>>() {
            @Override
            public void onSuccess(PageResponse<UserResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    allUsers.addAll(data.getContent());
                    totalPages = data.getTotalPages();
                    applyFilter(currentFilter);
                    showLoading(false);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    isLoading = false;
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    showLoading(false);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                    isLoading = false;
                    updateEmptyState();
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
                    allUsers.addAll(data.getContent());
                    currentPage = nextPage;
                    applyFilter(currentFilter);
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

    private void showLoading(boolean show) {
        if (progressBar != null) progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rvUsers != null) rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(filteredUsers.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void updateCountLabel() {
        if (tvUserCount != null) {
            long active = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getActive())).count();
            tvUserCount.setText(allUsers.size() + " người dùng · " + active + " đang hoạt động");
        }
    }

    @Override
    public void onToggleActive(int userId) {
        adminRepository.toggleUserActive(userId, new AdminRepository.AdminCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    // Cập nhật trong allUsers
                    for (int i = 0; i < allUsers.size(); i++) {
                        if (allUsers.get(i).getId() == userId) {
                            allUsers.set(i, data);
                            break;
                        }
                    }
                    applyFilter(currentFilter);
                    Toast.makeText(requireContext(),
                            Boolean.TRUE.equals(data.getActive()) ? "Đã mở khóa tài khoản" : "Đã khóa tài khoản",
                            Toast.LENGTH_SHORT).show();
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
                                for (int i = 0; i < allUsers.size(); i++) {
                                    if (allUsers.get(i).getId() == userId) {
                                        allUsers.set(i, data);
                                        break;
                                    }
                                }
                                applyFilter(currentFilter);
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
