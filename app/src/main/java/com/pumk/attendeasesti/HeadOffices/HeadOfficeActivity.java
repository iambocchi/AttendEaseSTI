package com.pumk.attendeasesti.HeadOffices;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import com.pumk.attendeasesti.HeadOffices.headoffice_fragments.*;
import com.pumk.attendeasesti.Authentications.LoginActivity;
import com.pumk.attendeasesti.R;

public class HeadOfficeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.headoffice_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // DRAWER TOGGLE
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // DEFAULT FRAGMENT
        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            new HeadOfficeProfileFragment()
                    )
                    .commit();
        }

        // NAVIGATION
        navigationView.setNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.profile) {

                selectedFragment = new HeadOfficeProfileFragment();

            } else if (id == R.id.teacher_attendance) {

                selectedFragment = new HeadOfficeTeacherAttendanceFragment();

            } else if (id == R.id.teacher_management) {

                selectedFragment = new HeadOfficeTeacherManagementFragment();

            } else if (id == R.id.calendar) {

                selectedFragment = new HeadOfficeCalendarFragment();

            } else if (id == R.id.logout) {

                Intent intent = new Intent(
                        HeadOfficeActivity.this,
                        LoginActivity.class
                );

                startActivity(intent);
                finish();
            }

            // LOAD FRAGMENT
            if (selectedFragment != null) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                selectedFragment
                        )
                        .commit();
            }

            drawerLayout.closeDrawers();

            return true;
        });
    }
}