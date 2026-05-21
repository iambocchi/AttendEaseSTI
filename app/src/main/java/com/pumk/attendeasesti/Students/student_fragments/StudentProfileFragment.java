package com.pumk.attendeasesti.Students.student_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pumk.attendeasesti.R;

public class StudentProfileFragment extends Fragment {


    TextView nametext, campustext, academic_leveltext, sectiontext, programtext, year_leveltext, locationtext, idtext, emailtext;

    public StudentProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Connect fragment to XML
        View view = inflater.inflate(R.layout.studentprofile, container, false);

        // Initialize UI components
        nametext = view.findViewById(R.id.student_name);
        campustext = view.findViewById(R.id.student_campus);
        academic_leveltext = view.findViewById(R.id.student_academic_level);
        idtext = view.findViewById(R.id.student_id);
        sectiontext = view.findViewById(R.id.student_section);
        programtext = view.findViewById(R.id.student_program);
        year_leveltext = view.findViewById(R.id.yearlevel_section);
        locationtext = view.findViewById(R.id.student_location);
        emailtext = view.findViewById(R.id.student_email); // Ensure this ID matches your XML (even if it says 'teacher')

        // Fetch and display data
        getCurrentUserData();

        return view;
    }

    private void getCurrentUserData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) return;

        String email = currentUser.getEmail();

        // Fetching from "student" collection where email matches
        db.collection("students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        String name = doc.getString("name");
                        String campus = doc.getString("campus");
                        String academicLevel = doc.getString("academic_level"); // fixed case
                        String section = doc.getString("section");
                        String program = doc.getString("program");
                        String yearLevel = doc.getString("year_level");
                        String location = doc.getString("location");
                        String id = doc.getString("student_id"); // fixed: read as String, not Long


                        // Update UI with the retrieved data
                        inflateProfile(name, campus, academicLevel, section, program, yearLevel, location, id, email);

                    } else {
                        Toast.makeText(requireContext(), "Student data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching student: " + e.getMessage()));
    }

    // Method to populate the TextViews
    private void inflateProfile(String name, String campus, String academicLevel, String section,
                                String program, String year_level, String location, String id, String email) {

        nametext.setText(name != null ? name : "N/A");
        campustext.setText(campus != null ? campus : "N/A");
        academic_leveltext.setText(academicLevel != null ? academicLevel : "N/A");
        sectiontext.setText(section != null ? section : "N/A");
        programtext.setText(program != null ? program : "N/A");
        year_leveltext.setText(year_level != null ? year_level : "N/A");
        locationtext.setText(location != null ? location : "N/A");
        idtext.setText(id != null ? id : "N/A");
        emailtext.setText(email != null ? email : "N/A");
    }
}