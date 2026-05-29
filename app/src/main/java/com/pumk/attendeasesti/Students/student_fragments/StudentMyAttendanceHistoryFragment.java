package com.pumk.attendeasesti.Students.student_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.AttendanceHistoryModel;
import com.pumk.attendeasesti.Students.student_adapters.StudentAttendanceHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentMyAttendanceHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AutoCompleteTextView monthDropdown;
    private StudentAttendanceHistoryAdapter adapter;
    private FirebaseFirestore db;

    // Full list kept so month filter doesn't need a re-fetch
    private List<AttendanceHistoryModel> allRecords = new ArrayList<>();

    public StudentMyAttendanceHistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.student_myattendance_history,
                container,
                false
        );

        db            = FirebaseFirestore.getInstance();
        recyclerView  = view.findViewById(R.id.recyclerTeacherSubjectToday); // your XML id
        monthDropdown = view.findViewById(R.id.userType);

        adapter = new StudentAttendanceHistoryAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Month dropdown
        String[] months = {
                "All", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_selected_item,
                months
        );
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        monthDropdown.setAdapter(monthAdapter);
        monthDropdown.setText("All", false);

        // Filter list when month is selected
        monthDropdown.setOnItemClickListener((parent, v, position, id) -> {
            String selected = parent.getItemAtPosition(position).toString();
            filterByMonth(selected);
        });

        loadAttendanceHistory();

        return view;
    }

    private void loadAttendanceHistory() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Step 1: Find the student document by email
        db.collection("students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(studentSnapshots -> {
                    if (studentSnapshots.isEmpty()) return;

                    String studentDocId = studentSnapshots.getDocuments().get(0).getId();

                    // Step 2: Fetch all Attendance subcollection documents
                    db.collection("students")
                            .document(studentDocId)
                            .collection("Attendance")
                            .addSnapshotListener((snapshots, error) -> {
                                if (error != null || snapshots == null) return;

                                allRecords.clear();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    String date      = doc.getString("date");
                                    String dayofweek = doc.getString("dayofweek");
                                    String month     = doc.getString("month");
                                    String status    = doc.getString("status");
                                    String subject   = doc.getString("subject");

                                    allRecords.add(new AttendanceHistoryModel(
                                            date, dayofweek, month, status, subject
                                    ));
                                }

                                // Sort by date descending — newest first
                                allRecords.sort((a, b) -> {
                                    if (a.getDate() == null || b.getDate() == null) return 0;
                                    return b.getDate().compareTo(a.getDate());
                                });

                                // Apply current month filter
                                String currentMonth = monthDropdown.getText().toString();
                                filterByMonth(currentMonth);
                            });
                });
    }

    private void filterByMonth(String month) {
        if (month == null || month.equals("All") || month.isEmpty()) {
            adapter.updateList(allRecords);
        } else {
            List<AttendanceHistoryModel> filtered = new ArrayList<>();
            for (AttendanceHistoryModel record : allRecords) {
                if (month.equalsIgnoreCase(record.getMonth())) {
                    filtered.add(record);
                }
            }
            adapter.updateList(filtered);
        }
    }
}