package com.example.minion_project;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivity extends AppCompatActivity {
    // a basic activity
    // TODO: IMPLEMENT the bottom naviagtion for user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView2);// Use the correct ID
        NavController navController = navHostFragment.getNavController();

        // Now you can use navController for navigation actions
        BottomNavigationView bottomNavigationView = findViewById(R.id.userBottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

}