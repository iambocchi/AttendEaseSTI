package com.pumk.attendeasesti.Teachers.teacher_fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.pumk.attendeasesti.Teachers.SubjectModel;
import com.pumk.attendeasesti.Teachers.teacher_adapters.TeacherMarkAttendanceAdapter;
import com.pumk.attendeasesti.Teachers.teacher_adapters.TeacherSubjectTodayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeacherStudentAttendanceFragment extends Fragment {

    private static final String TAG = "MarkAttendance";

    private RecyclerView recyclerSubjects, recyclerStudents;
    private SearchView searchView;
    private TeacherSubjectTodayAdapter subjectAdapter;
    private TeacherMarkAttendanceAdapter studentAdapter;
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

        db = FirebaseFirestore.getInstance();

        recyclerSubjects = view.findViewById(R.id.recyclerTeacherSubjectToday);
        recyclerStudents = view.findViewById(R.id.recyclerTeachersStudentAttendance);
        searchView       = view.findViewById(R.id.searchMarkAttendance);

        subjectAdapter = new TeacherSubjectTodayAdapter(new ArrayList<>());
        recyclerSubjects.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSubjects.setAdapter(subjectAdapter);

        studentAdapter = new TeacherMarkAttendanceAdapter(new ArrayList<>());
        recyclerStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerStudents.setAdapter(studentAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                studentAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                studentAdapter.getFilter().filter(newText);
                return false;
            }
        });

        loadTodaySubjectsThenStudents();

        return view;
    }

    private void loadTodaySubjectsThenStudents() {
        String teacherEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Match Firestore format: "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        String todayAbbrev = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date());

        Log.d(TAG, "Teacher email: " + teacherEmail + " | Today: " + todayAbbrev);

        db.collection("teachers")
                .whereEqualTo("email", teacherEmail)
                .get()
                .addOnSuccessListener(teacherSnapshots -> {
                    if (teacherSnapshots.isEmpty()) {
                        Log.w(TAG, "No teacher found for email: " + teacherEmail);
                        return;
                    }

                    String teacherDocId = teacherSnapshots.getDocuments().get(0).getId();
                    Log.d(TAG, "Teacher doc ID: " + teacherDocId);

                    db.collection("teachers")
                            .document(teacherDocId)
                            .collection("Subjects")
                            .addSnapshotListener((snapshots, error) -> {
                                if (error != null || snapshots == null) {
                                    Log.e(TAG, "Subjects error: " + (error != null ? error.getMessage() : "null"));
                                    return;
                                }

                                Log.d(TAG, "Total subjects: " + snapshots.size());

                                List<SubjectModel> todaySubjects = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    String subjectName = doc.getId();
                                    String day         = doc.getString("day");
                                    String time        = doc.getString("time");

                                    Log.d(TAG, "Subject: " + subjectName + " | day field: '" + day + "' | today: '" + todayAbbrev + "'");

                                    // Compare using abbreviated format to match Firestore "Mon", "Tue" etc.
                                    if (day != null && day.equalsIgnoreCase(todayAbbrev)) {
                                        todaySubjects.add(new SubjectModel(subjectName, day, time));
                                    }
                                }

                                Log.d(TAG, "Subjects today: " + todaySubjects.size());
                                subjectAdapter.updateList(todaySubjects);

                                if (!todaySubjects.isEmpty()) {
                                    String firstSubject = todaySubjects.get(0).getSubject_name();
                                    Log.d(TAG, "Loading students for subject: " + firstSubject);
                                    studentAdapter.setCurrentSubject(firstSubject);
                                    loadStudents();
                                } else {
                                    Log.w(TAG, "No subjects for today: " + todayAbbrev);
                                }
                            });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Teacher lookup failed: " + e.getMessage()));
    }

    private void loadStudents() {
        db.collection("students")
                .get()
                .addOnSuccessListener(snapshots -> {
                    Log.d(TAG, "Students fetched: " + snapshots.size());

                    List<StudentModel> studentList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        StudentModel student = new StudentModel();
                        student.setName(doc.getString("name"));
                        student.setEmail(doc.getString("email"));
                        student.setProgram(doc.getString("program"));
                        student.setCourse(doc.getString("course"));
                        student.setSection(doc.getString("section"));
                        student.setCampus(doc.getString("campus"));
                        student.setStatus(doc.getString("status"));
                        student.setStudent_id(doc.getString("student_id"));
                        student.setYear_level(doc.getString("year_level"));

                        studentList.add(student);
                        Log.d(TAG, "  → " + student.getName());
                    }

                    studentAdapter.updateList(studentList);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Students fetch failed: " + e.getMessage()));
    }
}