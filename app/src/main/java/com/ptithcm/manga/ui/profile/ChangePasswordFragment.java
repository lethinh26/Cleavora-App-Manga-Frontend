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
import com.ptithcm.manga.data.repository.UserRepository;
import com.ptithcm.manga.util.BackButtonHelper;

public class ChangePasswordFragment extends Fragment {
    private TextInputEditText etOldPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
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
        BackButtonHelper.addBackButton(this, view);
        userRepository = new UserRepository(requireContext());
        etOldPassword = view.findViewById(R.id.et_old_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = getText(etOldPassword);
        String newPassword = getText(etNewPassword);
        String confirmPassword = getText(etConfirmPassword);

        if (oldPassword.isEmpty()) {
            etOldPassword.setError("Vui lòng nhập mật khẩu cũ");
            etOldPassword.requestFocus();
            return;
        }
        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return;
        }
        if (oldPassword.equals(newPassword)) {
            etNewPassword.setError("Mật khẩu mới phải khác mật khẩu cũ");
            etNewPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        btnSave.setEnabled(false);
        userRepository.changePassword(oldPassword, newPassword, new UserRepository.UserCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                runOnUiThreadSafe(() -> {
                    Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThreadSafe(() -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), message != null ? message : "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private String getText(TextInputEditText input) {
        return input.getText() != null ? input.getText().toString().trim() : "";
    }

    private void runOnUiThreadSafe(Runnable action) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            if (isAdded()) action.run();
        });
    }
}
