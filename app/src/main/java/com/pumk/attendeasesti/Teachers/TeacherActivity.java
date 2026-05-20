package com.pumk.attendeasesti.Teachers;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.pumk.attendeasesti.Authentications.LoginActivity;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.teacher_fragments.*;

public class TeacherActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.teacher_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Drawer Toggle
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
                            new TeacherProfileFragment()
                    )
                    .commit();
        }

        // NAVIGATION
        navigationView.setNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.profile) {

                selectedFragment = new TeacherProfileFragment();

            } else if (id == R.id.student_attendance) {

                selectedFragment = new TeacherStudentAttendanceFragment();

            } else if (id == R.id.my_schedule) {

                selectedFragment = new TeacherScheduleFragment();

            } else if (id == R.id.absence_request) {

                selectedFragment = new TeacherAbsenceFragment();

            } else if (id == R.id.logout) {

                Intent intent = new Intent(
                        TeacherActivity.this,
                        LoginActivity.class
                );

                startActivity(intent);
                finish();
            }

            // LOAD FRAGMENT
            if (selectedFragment != null) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            drawerLayout.closeDrawers();

            return true;
        });
    }
}