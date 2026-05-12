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
import com.ptithcm.manga.data.model.response.LoginResponse;
import com.ptithcm.manga.data.repository.AuthRepository;

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository(requireContext());

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> handleLogin());

        view.findViewById(R.id.tv_go_register).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_register));
    }

    private void handleLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

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

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        authRepository.login(email, password, new AuthRepository.AuthCallback<LoginResponse>() {
            @Override
            public void onSuccess(String message, LoginResponse data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_login_to_home);
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText(R.string.login);
                });
            }
        });
    }
}
