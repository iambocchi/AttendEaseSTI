package com.pumk.attendeasesti.Teachers.teacher_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        recyclerView = view.findViewById(R.id.recyclerTeachersMySchedule);

        adapter = new TeacherScheduleAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadSubjects();

        return view;
    }

    private void loadSubjects() {
        // Step 1: Get the logged-in teacher's email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Step 2: Find the teacher document that matches the email
        db.collection("teachers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(teacherSnapshots -> {
                    if (teacherSnapshots.isEmpty()) return;

                    // Step 3: Get the teacher's document ID (their UID)
                    String teacherDocId = teacherSnapshots.getDocuments().get(0).getId();

                    // Step 4: Fetch the Subjects subcollection using that document ID
                    db.collection("teachers")
                            .document(teacherDocId)
                            .collection("Subjects")
                            .addSnapshotListener((snapshots, error) -> {
                                if (error != null || snapshots == null) return;

                                List<SubjectModel> subjectList = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    // Document ID = subject name, fields = day, time
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