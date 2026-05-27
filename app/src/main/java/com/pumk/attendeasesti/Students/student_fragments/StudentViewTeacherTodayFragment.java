package com.pumk.attendeasesti.Students.student_fragments;

import android.os.Bundle;
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
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.student_adapters.StudentViewTeacherTodayAdapter;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class StudentViewTeacherTodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private StudentViewTeacherTodayAdapter adapter;
    private FirebaseFirestore db;

    public StudentViewTeacherTodayFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.student_view_teacher_today,
                container,
                false
        );

        db           = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewTeachers);  // add to your XML
        searchView   = view.findViewById(R.id.searchViewTeacher);    // add to your XML

        adapter = new StudentViewTeacherTodayAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadPresentTeachers();

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

    private void loadPresentTeachers() {
        // Only fetch teachers whose status is "Present"
        db.collection("teachers")
                .whereEqualTo("status", "Present")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    List<TeacherModel> teacherList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String name       = doc.getString("name");
                        String email      = doc.getString("email");
                        String department = doc.getString("department");
                        String campus     = doc.getString("campus");
                        String status     = doc.getString("status");

                        TeacherModel teacher = new TeacherModel(name, email, department, campus,status, 0);
                        teacherList.add(teacher);
                    }

                    adapter.updateList(teacherList);
                });
    }
}