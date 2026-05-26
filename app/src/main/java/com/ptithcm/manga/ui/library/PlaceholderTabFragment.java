package com.ptithcm.manga.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.manga.R;

public class PlaceholderTabFragment extends Fragment {

    private final String tabName;

    public PlaceholderTabFragment(String tabName) {
        this.tabName = tabName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_tab, container, false);
        TextView tvEmpty = view.findViewById(R.id.tv_empty);
        tvEmpty.setText(tabName + " — sắp có");
        tvEmpty.setVisibility(View.VISIBLE);
        return view;
    }
}
