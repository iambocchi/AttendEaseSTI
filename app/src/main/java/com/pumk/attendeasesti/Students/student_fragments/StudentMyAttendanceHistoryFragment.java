package com.pumk.attendeasesti.Students.student_fragments;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pumk.attendeasesti.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StudentMyAttendanceHistoryFragment extends Fragment {

    public StudentMyAttendanceHistoryFragment() {
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
                R.layout.student_myattendance_history,
                container,
                false
        );

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dayFormat.format(calendar.getTime());

        Toast.makeText(getContext(), formattedDate +" "+ dayOfWeek, Toast.LENGTH_SHORT).show();



        return view;
    }
}