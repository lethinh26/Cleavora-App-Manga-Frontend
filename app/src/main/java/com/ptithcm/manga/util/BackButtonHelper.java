package com.ptithcm.manga.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ptithcm.manga.R;

public class BackButtonHelper {
    public static void addBackButton(Fragment fragment, View root) {
        if (root == null || root.findViewById(R.id.btn_back) != null) return;

        ViewGroup container = findHeaderContainer(root);
        if (container == null) return;

        ImageView backButton = new ImageView(root.getContext());
        backButton.setId(R.id.btn_back);
        backButton.setImageResource(android.R.drawable.ic_menu_revert);
        backButton.setContentDescription("back");
        backButton.setPadding(dp(root, 8), dp(root, 8), dp(root, 8), dp(root, 8));
        backButton.setBackgroundResource(R.drawable.bg_rounded_surface);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(root, 40), dp(root, 40));
        params.setMargins(0, 0, 0, dp(root, 12));
        container.addView(backButton, 0, params);
    }

    private static ViewGroup findHeaderContainer(View root) {
        if (root instanceof LinearLayout && ((LinearLayout) root).getOrientation() == LinearLayout.VERTICAL) {
            return (ViewGroup) root;
        }

        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof LinearLayout && ((LinearLayout) child).getOrientation() == LinearLayout.VERTICAL) {
                    return (ViewGroup) child;
                }
            }
        }
        return null;
    }

    private static int dp(View view, int value) {
        return (int) (value * view.getResources().getDisplayMetrics().density + 0.5f);
    }
}
