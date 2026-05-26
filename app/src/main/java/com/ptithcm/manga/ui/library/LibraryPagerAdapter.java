package com.ptithcm.manga.ui.library;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LibraryPagerAdapter extends FragmentStateAdapter {

    public LibraryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FavoritesTabFragment();
            case 1:
                return new PlaceholderTabFragment("Đang theo dõi");
            case 2:
                return new PlaceholderTabFragment("Lịch sử");
            default:
                return new FavoritesTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Favorites, Following, History
    }
}
