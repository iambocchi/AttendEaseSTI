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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.StudentModel;
import com.pumk.attendeasesti.Teachers.teacher_adapters.TeacherAbsentRequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class TeacherAbsenceFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private TeacherAbsentRequestAdapter adapter;
    private FirebaseFirestore db;

    public TeacherAbsenceFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.teacher_absence_request,
                container,
                false
        );

        db           = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerAbsentStudents);
        searchView   = view.findViewById(R.id.searchAbsentStudents); // fixed: was R.id.status_present_absent

        adapter = new TeacherAbsentRequestAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadAbsentStudents();

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

    private void loadAbsentStudents() {
        String teacherEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("teachers")
                .whereEqualTo("email", teacherEmail)
                .get()
                .addOnSuccessListener(teacherSnapshots -> {
                    if (teacherSnapshots.isEmpty()) return;

                    String department = teacherSnapshots.getDocuments().get(0).getString("department");
                    if (department == null) return;

                    db.collection("students")
                            .whereEqualTo("status", "absent")
                            .addSnapshotListener((snapshots, error) -> {
                                if (error != null || snapshots == null) return;

                                List<StudentModel> studentList = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    StudentModel student = doc.toObject(StudentModel.class);
                                    studentList.add(student);
                                }

                                adapter.updateList(studentList);
                            });
                });
    }
}