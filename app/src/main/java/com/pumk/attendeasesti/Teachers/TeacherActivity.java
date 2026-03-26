package com.pumk.attendeasesti.Teachers;

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

public class TeacherActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout container; // Better to define this globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Ensure this layout contains the DrawerLayout and the FrameLayout!
        setContentView(R.layout.teacher_main);

        // Initialize the container once
        container = findViewById(R.id.fragment_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbar); // Make sure this ID matches your XML
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            View defaultView = getLayoutInflater().inflate(R.layout.headoffice_calendar, container, false);
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
                inflateLayout(R.layout.teacher_profile);

            } else if (id == R.id.student_attendance) {
                inflateLayout(R.layout.teacher_student_attendance);

            } else if (id == R.id.my_schedule) {
//                inflateLayout(R.layout.studentschedule1);

            } else if (id == R.id.absence_request) {
//                inflateLayout(R.layout.############);

            } else if (id == R.id.logout) {
                Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
                startActivity(intent);
                // Add your logout method here
//                handleLogout();
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