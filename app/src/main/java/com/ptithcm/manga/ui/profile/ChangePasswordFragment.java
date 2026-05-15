package com.ptithcm.manga.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.request.ChangePassRequest;
import com.ptithcm.manga.data.repository.UserRepository;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnSave;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(requireContext());

        etOldPassword = view.findViewById(R.id.et_old_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> attemptChangePassword());
    }

    private void attemptChangePassword() {
        String oldPassword = etOldPassword.getText() != null ? etOldPassword.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (oldPassword.isEmpty() || oldPassword.length() < 6) {
            etOldPassword.setError("Mật khẩu cũ phải tối thiểu 6 ký tự");
            return;
        }

        if (newPassword.isEmpty() || newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu mới phải tối thiểu 6 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        setLoading(true);
        ChangePassRequest request = new ChangePassRequest(oldPassword, newPassword);

        userRepository.changePassword(request, new UserRepository.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
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
        btnSave.setText(loading ? "Đang xử lý..." : getString(R.string.save));
    }
}
