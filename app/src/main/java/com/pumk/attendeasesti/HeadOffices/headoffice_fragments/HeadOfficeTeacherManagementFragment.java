package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pumk.attendeasesti.HeadOffices.headoffice_adapters.HeadOfficeManagementAdapter;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.ArrayList;
import java.util.List;
// ... existing imports ...

public class HeadOfficeTeacherManagementFragment extends Fragment {

    private FloatingActionButton fabTeacher;
    private RecyclerView recyclerViewManagement;
    private HeadOfficeManagementAdapter adapter;
    private List<TeacherModel> teacherList = new ArrayList<>();
    private FirebaseFirestore db;

    public HeadOfficeTeacherManagementFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.headoffice_teacher_management, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewManagement = view.findViewById(R.id.recyclerViewTeachers);
        recyclerViewManagement.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new HeadOfficeManagementAdapter(teacherList, teacher -> {
            // Handle item click if needed
        });
        recyclerViewManagement.setAdapter(adapter);

        // CORRECT WAY: Initialize FAB from the inflated view
        fabTeacher = view.findViewById(R.id.floatingRegisterTeacher);
        fabTeacher.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HeadOfficeRegisterTeacherFragment())
                    .addToBackStack(null)
                    .commit();
        });

        fetchTeachers();
        return view;
    }

    // Removed loadFragment() from onStart() as it's now handled in onCreateView

    private void fetchTeachers() {
        db.collection("teachers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    teacherList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String department = doc.getString("department");
                        String campus = doc.getString("campus");
//                        Long idLong   = doc.getLong("teacher_id"); // id is a number in Firestore
//                        String teacher_id     = idLong != null ? String.valueOf(idLong) : "";
                        Long teacherIdLong = doc.getLong("teacher_id");
                        int teacher_id = (teacherIdLong != null) ? teacherIdLong.intValue() : 0;
//test
                        teacherList.add(new TeacherModel(name, email, department, campus, teacher_id));
                    }
                    adapter.updateList(teacherList);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching teachers", e));
    }
}