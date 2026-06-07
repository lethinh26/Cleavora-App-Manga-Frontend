package com.ptithcm.manga;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.manga.data.local.TokenManager;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Top-level destinations = bottom nav tabs (no back arrow)
        Set<Integer> topLevelIds = new HashSet<>();
        topLevelIds.add(R.id.nav_home);
        topLevelIds.add(R.id.nav_genre);
        topLevelIds.add(R.id.nav_search);
        topLevelIds.add(R.id.nav_library);
        topLevelIds.add(R.id.nav_profile);

        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(topLevelIds).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        TokenManager tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            navController.navigate(R.id.nav_login);
        }

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();

            // Top-level tabs: show bottom nav, hide toolbar
            boolean isTopLevel = topLevelIds.contains(id);
            // Auth screens: hide both
            boolean isAuth = (id == R.id.nav_login || id == R.id.nav_register);

            if (isTopLevel) {
                bottomNav.setVisibility(android.view.View.VISIBLE);
                toolbar.setVisibility(android.view.View.GONE);
            } else if (isAuth) {
                bottomNav.setVisibility(android.view.View.GONE);
                toolbar.setVisibility(android.view.View.GONE);
            } else {
                // Sub-screens: show toolbar with back arrow, hide bottom nav
                bottomNav.setVisibility(android.view.View.GONE);
                toolbar.setVisibility(android.view.View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}