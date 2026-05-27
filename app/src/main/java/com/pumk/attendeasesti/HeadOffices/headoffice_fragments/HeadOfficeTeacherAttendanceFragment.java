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
import com.pumk.attendeasesti.HeadOffices.headoffice_adapters.HeadOfficeTeacherAttendanceAdapter;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HeadOfficeTeacherAttendanceFragment extends Fragment {

    private RecyclerView recyclerViewAttendance;
    private SearchView searchView;
    private HeadOfficeTeacherAttendanceAdapter adapter;
    private FirebaseFirestore db;

    // Today's date in the same format as Firestore document IDs: "May 27, 2026"
    private final String todayDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());

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

        db                     = FirebaseFirestore.getInstance();
        recyclerViewAttendance = view.findViewById(R.id.recyclerTeachersAttendance);
        searchView             = view.findViewById(R.id.searchTeachers);

        adapter = new HeadOfficeTeacherAttendanceAdapter(new ArrayList<>());
        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAttendance.setAdapter(adapter);

        // Load today's attendance from the Attendance subcollection
        loadTeachersWithTodayAttendance();

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
     * Fetches all teachers, then reads each teacher's
     * Attendance/{todayDate} doc for today's status.
     * Same pattern as HeadOfficeCalendarFragment but fixed to today only.
     */
    private void loadTeachersWithTodayAttendance() {
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

                        // Read today's attendance from subcollection
                        db.collection("teachers")
                                .document(docId)
                                .collection("Attendance")
                                .document(todayDate) // e.g. "May 27, 2026"
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