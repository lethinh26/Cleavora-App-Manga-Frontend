package com.ptithcm.manga.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.UserResponse;
import com.ptithcm.manga.data.repository.AuthRepository;
import com.ptithcm.manga.data.repository.UserRepository;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvDisplayName, tvEmail;
    private UserRepository userRepository;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());

        imgAvatar = view.findViewById(R.id.iv_avatar);
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvEmail = view.findViewById(R.id.tv_email);

        if (tokenManager.isLoggedIn()) {
            loadProfile();
        }

        setupMenuListeners(view);
    }

    private void loadProfile() {
        userRepository.getProfile(new UserRepository.UserCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvDisplayName.setText(data.getDisplayName());
                    tvEmail.setText(data.getEmail());

                    if (data.getAvatarUrl() != null && !data.getAvatarUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(data.getAvatarUrl())
                                .placeholder(R.drawable.bg_placeholder_avatar)
                                .into(imgAvatar);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    tvDisplayName.setText(tokenManager.getDisplayName());
                    tvEmail.setText(tokenManager.getEmail());
                });
            }
        });
    }

    private void setupMenuListeners(View view) {
        View menuEditProfile = view.findViewById(R.id.btn_edit_profile);
        if (menuEditProfile != null) {
            menuEditProfile.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_profile_to_edit));
        }

        View menuChangePassword = view.findViewById(R.id.btn_change_password);
        if (menuChangePassword != null) {
            menuChangePassword.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_profile_to_change_password));
        }

        View menuMyMangas = view.findViewById(R.id.btn_my_mangas);
        if (menuMyMangas != null) {
            menuMyMangas.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_profile_to_my_mangas));
        }

        View menuAdmin = view.findViewById(R.id.btn_admin_panel);
        if (menuAdmin != null) {
            String role = tokenManager.getRole();
            if ("ADMIN".equals(role) || "SUPERADMIN".equals(role)) {
                menuAdmin.setVisibility(View.VISIBLE);
                menuAdmin.setOnClickListener(v ->
                        Navigation.findNavController(v).navigate(R.id.action_profile_to_admin_dashboard));
            } else {
                menuAdmin.setVisibility(View.GONE);
            }
        }

        View menuLogout = view.findViewById(R.id.btn_logout);
        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> {
                new AuthRepository(requireContext()).logout();
                Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_profile_to_login);
            });
        }
    }
}
