package com.ptithcm.manga.ui.auth;

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
import com.ptithcm.manga.data.repository.AuthRepository;

public class RegisterFragment extends Fragment {

    private TextInputEditText etDisplayName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository(requireContext());

        etDisplayName = view.findViewById(R.id.et_display_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> handleRegister());

        view.findViewById(R.id.tv_go_login).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_register_to_login));
    }

    private void handleRegister() {
        String displayName = etDisplayName.getText() != null ? etDisplayName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (displayName.isEmpty()) {
            etDisplayName.setError("Vui lòng nhập tên hiển thị");
            etDisplayName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");

        authRepository.register(email, password, displayName, new AuthRepository.AuthCallback<Object>() {
            @Override
            public void onSuccess(String message, Object data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_register_to_login);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(true);
                    btnRegister.setText(R.string.register);
                });
            }
        });
    }
}
