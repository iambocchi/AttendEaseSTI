package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

public class HeadOfficeCalendarFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private HeadOfficeCalendarAdapter adapter;
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerTeachersAttendanceCalendar);
        searchView   = view.findViewById(R.id.searchTeachersCalendar);

        adapter = new HeadOfficeCalendarAdapter(new ArrayList<>());
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        recyclerView.setNestedScrollingEnabled(false); // Add this line to ensure smooth scrolling
        recyclerView.setAdapter(adapter);

        loadTeachers();

        if (searchView != null) {
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
        }

        return view;
    }

    private void loadTeachers() {
        db.collection("teachers")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    List<TeacherModel> teacherList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String name       = doc.getString("name");
                        String email      = doc.getString("email");
                        String department = doc.getString("department");
                        String campus     = doc.getString("campus");
                        String status     = doc.getString("status");

                        TeacherModel teacher = new TeacherModel(name, email, department, campus, 0);
                        teacher.setStatus(status);
                        teacherList.add(teacher);
                    }

                    adapter.updateList(teacherList);
                });
    }
}