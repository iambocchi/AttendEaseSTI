package com.pumk.attendeasesti.Teachers.teacher_fragments;

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
import com.pumk.attendeasesti.Students.StudentModel;
import com.pumk.attendeasesti.Teachers.teacher_adapters.TeacherStudentAttendanceAdapter;

import java.util.ArrayList;
import java.util.List;

public class TeacherStudentAttendanceFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private TeacherStudentAttendanceAdapter adapter;
    private FirebaseFirestore db;

    public TeacherStudentAttendanceFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.teacher_student_attendance,
                container,
                false
        );

        db            = FirebaseFirestore.getInstance();
        recyclerView  = view.findViewById(R.id.recyclerTeachersStudentAttendance);       // add to your XML
        searchView    = view.findViewById(R.id.searchMarkAttendance);    // already in your XML

        // Set up RecyclerView
        adapter = new TeacherStudentAttendanceAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load only BS Computer Science students
        loadStudents();

        // Wire up SearchView
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

    private void loadStudents() {
        // Filter Firestore to only fetch students whose program is "BS Computer Science"
        db.collection("students")
                .whereEqualTo("program", "BS Computer Science")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    List<StudentModel> studentList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        StudentModel student = doc.toObject(StudentModel.class);
                        studentList.add(student);
                    }

                    adapter.updateList(studentList);
                });
    }
}