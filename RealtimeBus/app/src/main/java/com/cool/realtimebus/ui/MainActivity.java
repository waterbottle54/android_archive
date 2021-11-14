package com.cool.realtimebus.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.cool.realtimebus.R;
import com.cool.realtimebus.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
    }

    @Override
    public boolean onNavigateUp() {
        return navController.navigateUp() || super.onNavigateUp();
    }
}