package com.pumk.attendeasesti.Students;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pumk.attendeasesti.Authentications.LoginActivity;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.student_fragments.*;

public class StudentActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.student_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ✅ Load the student's name into the nav header
        loadNavBarName();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new StudentProfileFragment())
                    .commit();
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.profile) {
                selectedFragment = new StudentProfileFragment();
            } else if (id == R.id.my_schedule) {
                selectedFragment = new StudentMyScheduleFragment();
            } else if (id == R.id.my_attendance) {
                selectedFragment = new StudentMyScheduleFragment();
            } else if (id == R.id.view_teacher_today) {
                selectedFragment = new StudentViewTeacherTodayFragment();
            } else if (id == R.id.absent_request) {
                selectedFragment = new StudentAbsentRequestFragment();
            } else if (id == R.id.logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

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

    // ✅ Fetches the student's name from Firestore and sets it in the nav header
    private void loadNavBarName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String email = currentUser.getEmail();

        // Get the TextView from the nav header
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name); // ← your TextView ID in nav_header.xml

        FirebaseFirestore.getInstance()
                .collection("students")           // ← students collection for StudentActivity
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String name = snapshot.getDocuments().get(0).getString("name");
                        navName.setText(name != null ? name : "Student");
                    }
                })
                .addOnFailureListener(e -> Log.e("NavName", "Error: " + e.getMessage()));
    }
}