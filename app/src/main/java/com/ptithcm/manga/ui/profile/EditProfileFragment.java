package com.ptithcm.manga.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.local.TokenManager;
import com.ptithcm.manga.data.model.response.UploadResponse;
import com.ptithcm.manga.data.model.response.UserResponse;
import com.ptithcm.manga.data.repository.CloudRepository;
import com.ptithcm.manga.data.repository.UserRepository;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private CloudRepository cloudRepository;
    private UserRepository userRepository;

    private EditText edtName, edtEmail;
    private Button btnSave;
    private CircleImageView ivAvatar;
    private TextView btnChangeAvatar;
    private Uri selectedImageUri;
    private String currentAvatarUrl;
    private TokenManager tokenManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cloudRepository = new CloudRepository();
        userRepository = new UserRepository(requireContext());
        userRepository = new UserRepository(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());
        
        findId();

        // mở thư viện ảnh
        btnChangeAvatar.setOnClickListener(v -> openGallery());
        ivAvatar.setOnClickListener(v -> openGallery());

        // click vào button để xác nhận lưu
        btnSave.setOnClickListener(v -> handleSave(view));

        if (tokenManager.isLoggedIn()) {
            loadProfile();
        }
    }

    private void findId(){
        edtName = getView().findViewById(R.id.et_display_name);
        edtEmail = getView().findViewById(R.id.et_email);
        btnSave = getView().findViewById(R.id.btn_save);
        ivAvatar = getView().findViewById(R.id.iv_avatar);
        btnChangeAvatar = getView().findViewById(R.id.btn_change_avatar);
    }

    private void loadProfile() {
        userRepository.getProfile(new UserRepository.UserCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                if(!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    edtName.setText(data.getDisplayName());
                    edtEmail.setText(data.getEmail());

                    if (data.getAvatarUrl() != null && !data.getAvatarUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(data.getAvatarUrl())
                                .placeholder(R.drawable.bg_placeholder_avatar)
                                .into(ivAvatar);
                    }
                });

            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    edtName.setText(tokenManager.getDisplayName());
                    edtEmail.setText(tokenManager.getEmail());
                });
            }
        });
    }

    public void handleSave(View view){
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            cloudRepository.uploadImageToCloudinary(requireContext(), selectedImageUri, new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String newAvatarUrl = response.body().secure_url;
                        performUpdateProfile(view, name, email, newAvatarUrl);
                    } else {
                        Toast.makeText(getContext(), "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Log.e("Cloudinary", "Upload failed", t);
                    Toast.makeText(getContext(), "Lỗi khi tải ảnh!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            performUpdateProfile(view, name, email, currentAvatarUrl);
        }
    }

    private void performUpdateProfile(View view, String name, String email, String avatarUrl) {
        userRepository.updateProfile(name, email, avatarUrl, new UserRepository.UserCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse data) {
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.nav_profile);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Cập nhật thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).into(ivAvatar);
                }
            }
    );
}
