package com.pumk.attendeasesti.HeadOffices;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;

import com.pumk.attendeasesti.Logins.LoginActivity;
import com.pumk.attendeasesti.R;
// ... existing imports ...

public class HeadOfficeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout container; // Better to define this globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Ensure this layout contains the DrawerLayout and the FrameLayout!
        setContentView(R.layout.headoffice_main);

        // Initialize the container once
        container = findViewById(R.id.fragment_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbar); // Make sure this ID matches your XML
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            View defaultView = getLayoutInflater().inflate(R.layout.headoffice_profile, container, false);
            container.addView(defaultView);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Clear the frame before putting something new in
            container.removeAllViews();
            if (id == R.id.profile) {
                inflateLayout(R.layout.headoffice_profile);

            } else if (id == R.id.teacher_attendance) {
                inflateLayout(R.layout.teacher_student_attendance);

            } else if (id == R.id.teacher_management) {
                inflateLayout(R.layout.headoffice_teacher_management);

            } else if (id == R.id.calendar) {
                inflateLayout(R.layout.headoffice_calendar);

            } else if (id == R.id.logout) {
                // Usually a method to clear session and redirect to LoginActivity
                Intent intent = new Intent(HeadOfficeActivity.this, LoginActivity.class);
                startActivity(intent);
//                performLogout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // Helper method to keep your code clean
    private void inflateLayout(int layoutResId) {
        View view = getLayoutInflater().inflate(layoutResId, container, false);
        container.addView(view);
    }
}