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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentViewTeacherTodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private StudentViewTeacherTodayAdapter adapter;
    private FirebaseFirestore db;

    // Today's date matching Firestore Attendance document ID format
    private final String todayDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());

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
        recyclerView = view.findViewById(R.id.recyclerViewTeachers);
        searchView   = view.findViewById(R.id.searchViewTeacher);

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

    /**
     * Fetches all teachers, checks each one's Attendance/{todayDate} doc,
     * and only shows teachers whose status is "Present" for today.
     */
    private void loadPresentTeachers() {
        db.collection("teachers")
                .get()
                .addOnSuccessListener(teacherSnapshots -> {
                    int total = teacherSnapshots.size();
                    if (total == 0) return;

                    int[] completed = {0};
                    List<TeacherModel> presentTeachers = new ArrayList<>();

                    for (QueryDocumentSnapshot teacherDoc : teacherSnapshots) {
                        String docId      = teacherDoc.getId();
                        String name       = teacherDoc.getString("name");
                        String email      = teacherDoc.getString("email");
                        String department = teacherDoc.getString("department");
                        String campus     = teacherDoc.getString("campus");

                        // Check today's attendance doc for this teacher
                        db.collection("teachers")
                                .document(docId)
                                .collection("Attendance")
                                .document(todayDate)
                                .get()
                                .addOnSuccessListener(attendanceDoc -> {
                                    if (attendanceDoc.exists()) {
                                        String status = attendanceDoc.getString("status");
                                        // Only add if Present today
                                        if ("Present".equalsIgnoreCase(status)) {
                                            presentTeachers.add(new TeacherModel(
                                                    name, email, department, campus, status, 0
                                            ));
                                        }
                                    }

                                    completed[0]++;
                                    if (completed[0] == total) {
                                        adapter.updateList(presentTeachers);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    completed[0]++;
                                    if (completed[0] == total) {
                                        adapter.updateList(presentTeachers);
                                    }
                                });
                    }
                });
    }
}