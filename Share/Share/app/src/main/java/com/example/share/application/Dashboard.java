package com.example.share.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.share.R;
import com.example.share.connection.ServerAPI;
import com.example.share.constants.StaticData;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.share.databinding.ActivityDashboardBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dashboard extends AppCompatActivity {
    private static boolean isStarted = false;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;

    public static NavigationView navigationView;
    public static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDashboard.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_lend, R.id.nav_item ,R.id.nav_borrow)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Intent intent = getIntent();
        StaticData.username = intent.getStringExtra("USERNAME");
        StaticData.email = intent.getStringExtra("EMAIL");
        StaticData.uid = Integer.parseInt(intent.getStringExtra("UID"));
        StaticData.bid = Integer.parseInt(intent.getStringExtra("BID"));
        ServerAPI serverAPI = ServerAPI.getInstance();
        if (!isStarted) {
            serverAPI.start();
            isStarted = true;
        }

        View headerView = navigationView.getHeaderView(0);
        TextView nav_email = headerView.findViewById(R.id.nav_email);
        TextView nav_username = headerView.findViewById(R.id.nav_username);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            runOnUiThread(() -> {
                nav_username.setText(StaticData.username);
                nav_email.setText(StaticData.email);
            });
        });
        executorService.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}