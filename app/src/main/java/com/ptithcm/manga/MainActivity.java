package com.ptithcm.manga;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.manga.data.local.TokenManager;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);
        TokenManager tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            navController.navigate(R.id.nav_login);
        }

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.nav_login || id == R.id.nav_register
                    || id == R.id.nav_manga_detail
                    || id == R.id.nav_edit_profile || id == R.id.nav_change_password
                    || id == R.id.nav_admin_dashboard || id == R.id.nav_admin_manga_list
                    || id == R.id.nav_admin_manga_form || id == R.id.nav_admin_chapter_form
                    || id == R.id.nav_admin_user_list || id == R.id.nav_admin_genre
                    || id == R.id.nav_admin_pending
                    || id == R.id.nav_submit_manga || id == R.id.nav_my_mangas) {
                bottomNav.setVisibility(android.view.View.GONE);
            } else {
                bottomNav.setVisibility(android.view.View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}