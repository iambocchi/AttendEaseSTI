package com.pumk.attendeasesti.Students.student_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.AttendanceHistoryModel;
import com.pumk.attendeasesti.Students.student_adapters.StudentAttendanceHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentMyAttendanceHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAttendanceHistoryAdapter adapter;

    private AutoCompleteTextView monthDropdown;

    private final List<AttendanceHistoryModel> attendanceList = new ArrayList<>();
    private final List<AttendanceHistoryModel> filteredList = new ArrayList<>();

    public StudentMyAttendanceHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_student_my_attendance_history,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerTeacherSubjectToday);
        monthDropdown = view.findViewById(R.id.userType);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new StudentAttendanceHistoryAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        setupMonthDropdown();

        loadSampleData();

        // Show all data initially
        filteredList.addAll(attendanceList);
        adapter.updateList(filteredList);
    }

    private void setupMonthDropdown() {

        String[] months = {
                "All",
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        };

        ArrayAdapter<String> monthAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        months
                );

        monthDropdown.setAdapter(monthAdapter);

        monthDropdown.setOnItemClickListener((parent, view, position, id) -> {

            String selectedMonth = parent.getItemAtPosition(position).toString();

            filterAttendance(selectedMonth);
        });
    }

    private void filterAttendance(String month) {

        filteredList.clear();

        if (month.equalsIgnoreCase("All")) {

            filteredList.addAll(attendanceList);

        } else {

            for (AttendanceHistoryModel item : attendanceList) {

                if (item.getMonth() != null &&
                        item.getMonth().equalsIgnoreCase(month)) {

                    filteredList.add(item);
                }
            }
        }

        adapter.updateList(filteredList);
    }

    private void loadSampleData() {

        attendanceList.clear();

        attendanceList.add(new AttendanceHistoryModel(
                "May",
                "May 27, 2026",
                "Present",
                "Computer Programming"
        ));

        attendanceList.add(new AttendanceHistoryModel(
                "May",
                "May 25, 2026",
                "Late",
                "Database Management"
        ));

        attendanceList.add(new AttendanceHistoryModel(
                "April",
                "April 20, 2026",
                "Absent",
                "Networking"
        ));

        attendanceList.add(new AttendanceHistoryModel(
                "March",
                "March 11, 2026",
                "Excused",
                "Mobile Development"
        ));
    }
}