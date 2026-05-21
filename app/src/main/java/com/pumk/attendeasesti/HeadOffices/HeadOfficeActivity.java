package com.pumk.attendeasesti.HeadOffices;

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.nav_open,
                R.string.nav_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ✅ Load head office name into nav header
        loadNavBarName();

        // DEFAULT FRAGMENT
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HeadOfficeProfileFragment())
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
                startActivity(new Intent(HeadOfficeActivity.this, LoginActivity.class));
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

    // ✅ Fetches the head office user's name from Firestore and sets it in the nav header
    private void loadNavBarName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String email = currentUser.getEmail();

        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.nav_name); // ← match your nav_header.xml ID

        FirebaseFirestore.getInstance()
                .collection("headoffice")         // ← headoffice collection
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String name = snapshot.getDocuments().get(0).getString("name");
                        navName.setText(name != null ? name : "Head Office");
                    }
                })
                .addOnFailureListener(e -> Log.e("NavName", "Error: " + e.getMessage()));
    }
}