package com.pumk.attendeasesti.Students.student_fragments;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pumk.attendeasesti.R;

public class StudentViewTeacherTodayFragment extends Fragment {

    public StudentViewTeacherTodayFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Connect fragment to XML
        View view = inflater.inflate(
                R.layout.student_view_teacher_today,
                container,
                false
        );

        // Initialize views here
        // Example:
        // TextView name = view.findViewById(R.id.name);

        return view;
    }
}