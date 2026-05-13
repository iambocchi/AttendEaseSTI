package com.pumk.attendeasesti.Teachers.teacher_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pumk.attendeasesti.R;

public class TeacherStudentAttendanceFragment extends Fragment{
    public TeacherStudentAttendanceFragment() {
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
                R.layout.teacher_student_attendance,
                container,
                false
        );

        // Initialize views here
        // Example:
        // TextView name = view.findViewById(R.id.name);

        return view;
    }
}
