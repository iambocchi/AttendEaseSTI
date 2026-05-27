package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pumk.attendeasesti.HeadOffices.headoffice_adapters.HeadOfficeCalendarAdapter;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HeadOfficeCalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private HeadOfficeCalendarAdapter adapter;
    private FirebaseFirestore db;

    // Defaults to today
    private String selectedDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());

    public HeadOfficeCalendarFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.headoffice_calendar,
                container,
                false
        );

        db           = FirebaseFirestore.getInstance();
        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerTeachersAttendanceCalendar);
        searchView   = view.findViewById(R.id.searchTeachersCalendar);

        adapter = new HeadOfficeCalendarAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load today on open
        adapter.setCurrentDate(selectedDate);
        loadTeacherAttendanceForDate(selectedDate);

        // Calendar date change
        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            Date picked = new Date(year - 1900, month, dayOfMonth);
            selectedDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(picked);
            adapter.setCurrentDate(selectedDate);
            loadTeacherAttendanceForDate(selectedDate);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    /**
     * For each teacher, reads their Attendance doc for the selected date.
     * Shows "No Record" if no doc exists yet for that day.
     */
    private void loadTeacherAttendanceForDate(String date) {
        db.collection("teachers")
                .get()
                .addOnSuccessListener(teacherSnapshots -> {
                    List<TeacherModel> result = new ArrayList<>();
                    int total = teacherSnapshots.size();

                    if (total == 0) {
                        adapter.updateList(result);
                        return;
                    }

                    int[] completed = {0};

                    for (QueryDocumentSnapshot teacherDoc : teacherSnapshots) {
                        String docId      = teacherDoc.getId();
                        String name       = teacherDoc.getString("name");
                        String email      = teacherDoc.getString("email");
                        String department = teacherDoc.getString("department");
                        String campus     = teacherDoc.getString("campus");
                        Long   idLong     = teacherDoc.getLong("teacher_id");
                        int    teacherId  = idLong != null ? idLong.intValue() : 0;

                        db.collection("teachers")
                                .document(docId)
                                .collection("Attendance")
                                .document(date)
                                .get()
                                .addOnSuccessListener(attendanceDoc -> {
                                    String status = "No Record";
                                    if (attendanceDoc.exists()) {
                                        String s = attendanceDoc.getString("status");
                                        if (s != null) status = s;
                                    }

                                    result.add(new TeacherModel(
                                            name, email, department, campus, status, teacherId
                                    ));

                                    completed[0]++;
                                    if (completed[0] == total) adapter.updateList(result);
                                })
                                .addOnFailureListener(e -> {
                                    result.add(new TeacherModel(
                                            name, email, department, campus, "No Record", teacherId
                                    ));
                                    completed[0]++;
                                    if (completed[0] == total) adapter.updateList(result);
                                });
                    }
                });
    }
}