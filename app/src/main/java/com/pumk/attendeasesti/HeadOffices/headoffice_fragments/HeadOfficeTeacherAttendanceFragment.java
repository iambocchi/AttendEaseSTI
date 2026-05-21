package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

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
import com.pumk.attendeasesti.HeadOffices.headoffice_adapters.HeadOfficeTeacherAttendanceAdapter;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class HeadOfficeTeacherAttendanceFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private HeadOfficeTeacherAttendanceAdapter adapter;
    private FirebaseFirestore db;

    public HeadOfficeTeacherAttendanceFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.headoffice_teacher_attendance,
                container,
                false
        );

        db           = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerTeachers); // add this id to your XML
        searchView   = view.findViewById(R.id.searchTeachers);   // add this id to your XML

        // Set up RecyclerView
        adapter = new HeadOfficeTeacherAttendanceAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load from Firestore (real-time)
        loadTeachers();

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


//ETO GAGAWIN AFTER-------------------------------------------------------

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
//    private void loadTeachers() {
//        db.collection("teachers")
//                .addSnapshotListener((snapshots, error) -> {
//                    if (error != null || snapshots == null) return;
//
//                    List<TeacherModel> teacherList = new ArrayList<>();
//                    for (QueryDocumentSnapshot doc : snapshots) {
//                        TeacherModel teacher = doc.toObject(TeacherModel.class);
//                        teacherList.add(teacher);
//                    }
//
//                    adapter.updateList(teacherList);
//                });
//    }
}