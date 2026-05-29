package com.pumk.attendeasesti.Teachers.teacher_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.SubjectModel;
import com.pumk.attendeasesti.Teachers.teacher_adapters.TeacherScheduleAdapter;

import java.util.ArrayList;
import java.util.List;

public class TeacherScheduleFragment extends Fragment {

    private RecyclerView recyclerView;
    private TeacherScheduleAdapter adapter;
    private FirebaseFirestore db;

    // Teacher info views from your XML
    private TextView teacherName, teacherEmail, teacherDepartment;

    public TeacherScheduleFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.teacher_my_schedule,
                container,
                false
        );

        db = FirebaseFirestore.getInstance();

        // Bind teacher info views
        teacherName       = view.findViewById(R.id.teacher_name);
        teacherEmail      = view.findViewById(R.id.teacher_email);
        teacherDepartment = view.findViewById(R.id.teacher_department);

        recyclerView = view.findViewById(R.id.recyclerTeachersMySchedule);
        adapter = new TeacherScheduleAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadTeacherDataAndSubjects();

        return view;
    }

    private void loadTeacherDataAndSubjects() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("teachers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(teacherSnapshots -> {
                    if (teacherSnapshots.isEmpty()) return;

                    // Step 1: Fill teacher info card at the top
                    String name       = teacherSnapshots.getDocuments().get(0).getString("name");
                    String dept       = teacherSnapshots.getDocuments().get(0).getString("department");
                    String teacherDocId = teacherSnapshots.getDocuments().get(0).getId();

                    teacherName.setText(name != null ? name : "N/A");
                    teacherEmail.setText(email);
                    teacherDepartment.setText(dept != null ? dept : "N/A");

                    // Step 2: Fetch Subjects subcollection
                    db.collection("teachers")
                            .document(teacherDocId)
                            .collection("Subjects")
                            .addSnapshotListener((snapshots, error) -> {
                                if (error != null || snapshots == null) return;

                                List<SubjectModel> subjectList = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    String subjectName = doc.getId();
                                    String day         = doc.getString("day");
                                    String time        = doc.getString("time");
                                    subjectList.add(new SubjectModel(subjectName, day, time));
                                }

                                adapter.updateList(subjectList);
                            });
                });
    }
}