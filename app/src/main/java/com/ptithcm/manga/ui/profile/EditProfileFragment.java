package com.ptithcm.manga.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.request.UpdateProfileRequest;
import com.ptithcm.manga.data.model.response.UploadResponse;
import com.ptithcm.manga.data.model.response.UserResponse;
import com.ptithcm.manga.data.repository.UploadRepository;
import com.ptithcm.manga.data.repository.UserRepository;
import com.ptithcm.manga.data.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextInputEditText etDisplayName, etEmail;
    private MaterialButton btnSave;
    private View btnChangeAvatar;

    private UserRepository userRepository;
    private UploadRepository uploadRepository;
    private TokenManager tokenManager;

    private String currentAvatarUrl;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(requireContext()).load(uri).into(ivAvatar);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(requireContext());
        uploadRepository = new UploadRepository(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());

        ivAvatar = view.findViewById(R.id.iv_avatar);
        etDisplayName = view.findViewById(R.id.et_display_name);
        etEmail = view.findViewById(R.id.et_email);
        btnSave = view.findViewById(R.id.btn_save);
        btnChangeAvatar = view.findViewById(R.id.btn_change_avatar);

        loadCurrentProfile();

        btnChangeAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> onSaveClicked());
    }

    private void loadCurrentProfile() {
        userRepository.getProfile(new UserRepository.UserCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    etDisplayName.setText(data.getDisplayName());
                    etEmail.setText(data.getEmail());
                    currentAvatarUrl = data.getAvatarUrl();

                    if (currentAvatarUrl != null && !currentAvatarUrl.isEmpty()) {
                        Glide.with(requireContext())
                                .load(currentAvatarUrl)
                                .placeholder(R.drawable.bg_placeholder_avatar)
                                .into(ivAvatar);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    etDisplayName.setText(tokenManager.getDisplayName());
                    etEmail.setText(tokenManager.getEmail());
                    currentAvatarUrl = tokenManager.getAvatarUrl();
                });
            }
        });
    }

    private void onSaveClicked() {
        String displayName = etDisplayName.getText() != null ? etDisplayName.getText().toString().trim() : "";

        if (displayName.isEmpty()) {
            etDisplayName.setError("Tên hiển thị không được để trống");
            return;
        }

        setLoading(true);

        if (selectedImageUri != null) {
            uploadAvatarThenSave(displayName);
        } else {
            saveProfile(displayName, currentAvatarUrl);
        }
    }

    private void uploadAvatarThenSave(String displayName) {
        uploadRepository.uploadImage(selectedImageUri, "avatars")
                .enqueue(new Callback<ApiResponse<UploadResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UploadResponse>> call,
                                           Response<ApiResponse<UploadResponse>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String newAvatarUrl = response.body().getData().getImageUrl();
                            saveProfile(displayName, newAvatarUrl);
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                setLoading(false);
                                Toast.makeText(requireContext(), "Upload ảnh thất bại", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<UploadResponse>> call, Throwable t) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            setLoading(false);
                            Toast.makeText(requireContext(), "Lỗi kết nối khi upload ảnh", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void saveProfile(String displayName, String avatarUrl) {
        UpdateProfileRequest request = new UpdateProfileRequest(avatarUrl, displayName);

        userRepository.updateProfile(request, new UserRepository.UserCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                if (!isAdded()) return;
                tokenManager.saveUser(
                        data.getId(),
                        data.getEmail(),
                        data.getDisplayName(),
                        data.getRole(),
                        data.getAvatarUrl()
                );

                requireActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        btnSave.setText(loading ? "Đang lưu..." : getString(R.string.save));
    }
}
