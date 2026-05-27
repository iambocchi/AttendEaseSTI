package com.pumk.attendeasesti.Teachers.teacher_adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.StudentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeacherMarkAttendanceAdapter extends RecyclerView.Adapter<TeacherMarkAttendanceAdapter.StudentViewHolder>
        implements Filterable {

    private List<StudentModel> fullList;
    private List<StudentModel> filteredList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Persists selected status per student email for the entire session
    private final Map<String, String> selectedStatus = new HashMap<>();

    private final String todayDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
    private String currentSubject = "";

    public TeacherMarkAttendanceAdapter(List<StudentModel> list) {
        this.fullList     = new ArrayList<>(list);
        this.filteredList = new ArrayList<>(list);
    }

    public void updateList(List<StudentModel> newList) {
        this.fullList     = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);

        // Pre-load attendance status from Firestore for all students
        // so colors are restored correctly on bind
        for (StudentModel student : newList) {
            preloadAttendanceStatus(student.getEmail());
        }

        notifyDataSetChanged();
    }

    public void setCurrentSubject(String subject) {
        this.currentSubject = subject;
    }

    /**
     * Reads today's attendance doc for this student from Firestore
     * and caches the status in selectedStatus map.
     */
    private void preloadAttendanceStatus(String email) {
        if (email == null || email.isEmpty()) return;
        // Skip if already loaded
        if (selectedStatus.containsKey(email)) return;

        db.collection("students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) return;
                    String docId = snapshots.getDocuments().get(0).getId();

                    db.collection("students")
                            .document(docId)
                            .collection("Attendance")
                            .document(todayDate)
                            .get()
                            .addOnSuccessListener(attendanceDoc -> {
                                if (attendanceDoc.exists()) {
                                    String status = attendanceDoc.getString("status");
                                    if (status != null && !status.isEmpty()) {
                                        selectedStatus.put(email, status);
                                        // Refresh the specific item so color updates
                                        int pos = getPositionByEmail(email);
                                        if (pos >= 0) notifyItemChanged(pos);
                                    }
                                }
                            });
                });
    }

    private int getPositionByEmail(String email) {
        for (int i = 0; i < filteredList.size(); i++) {
            if (email.equals(filteredList.get(i).getEmail())) return i;
        }
        return -1;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_student_attendance_card, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = filteredList.get(position);
        String email = student.getEmail();

        holder.name.setText(student.getName() != null ? student.getName() : "N/A");
        String yearSection = (student.getYear_level() != null ? student.getYear_level() : "")
                + " - " + (student.getProgram() != null ? student.getProgram() : "");
        holder.yearLevel.setText(yearSection);

        // Restore color from cache (loaded from Firestore)
        String status = selectedStatus.getOrDefault(email, "");
        applyButtonColors(holder, status);

        holder.btnPresent.setOnClickListener(v -> onStatusSelected(holder, student, "Present"));
        holder.btnAbsent.setOnClickListener(v  -> onStatusSelected(holder, student, "Absent"));
        holder.btnLate.setOnClickListener(v    -> onStatusSelected(holder, student, "Late"));
        holder.btnExcused.setOnClickListener(v -> onStatusSelected(holder, student, "Excused"));
    }

    private void onStatusSelected(StudentViewHolder holder, StudentModel student, String status) {
        String email = student.getEmail();
        // 1. Update cache immediately so color survives scroll/rebind
        selectedStatus.put(email, status);
        // 2. Update UI immediately
        applyButtonColors(holder, status);
        // 3. Persist to Firestore
        saveAttendance(student, status);
    }

    private void applyButtonColors(StudentViewHolder holder, String status) {
        int gray   = Color.parseColor("#BDBDBD");
        int green  = Color.parseColor("#4CAF50");
        int red    = Color.parseColor("#F44336");
        int yellow = Color.parseColor("#FFC107");
        int blue   = Color.parseColor("#2196F3");

        holder.btnPresent.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gray));
        holder.btnAbsent.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gray));
        holder.btnLate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gray));
        holder.btnExcused.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gray));

        switch (status) {
            case "Present":
                holder.btnPresent.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green));
                break;
            case "Absent":
                holder.btnAbsent.setBackgroundTintList(android.content.res.ColorStateList.valueOf(red));
                break;
            case "Late":
                holder.btnLate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(yellow));
                break;
            case "Excused":
                holder.btnExcused.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue));
                break;
        }
    }

    private void saveAttendance(StudentModel student, String status) {
        String email = student.getEmail();
        if (email == null || email.isEmpty()) return;

        db.collection("students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) return;

                    String docId     = snapshots.getDocuments().get(0).getId();
                    String dayOfWeek = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date());
                    String month     = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());

                    Map<String, Object> attendanceData = new HashMap<>();
                    attendanceData.put("date",      todayDate);
                    attendanceData.put("dayofweek", dayOfWeek);
                    attendanceData.put("month",     month);
                    attendanceData.put("status",    status);
                    attendanceData.put("subject",   currentSubject);

                    db.collection("students")
                            .document(docId)
                            .collection("Attendance")
                            .document(todayDate)
                            .set(attendanceData);
                });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<StudentModel> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(fullList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (StudentModel s : fullList) {
                        boolean matchName    = s.getName()       != null && s.getName().toLowerCase().contains(query);
                        boolean matchSection = s.getSection()    != null && s.getSection().toLowerCase().contains(query);
                        boolean matchYear    = s.getYear_level() != null && s.getYear_level().toLowerCase().contains(query);
                        if (matchName || matchSection || matchYear) results.add(s);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<StudentModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, yearLevel, btnPresent, btnAbsent, btnLate, btnExcused;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name       = itemView.findViewById(R.id.tvStudentName);
            yearLevel  = itemView.findViewById(R.id.tvStudentYear);
            btnPresent = itemView.findViewById(R.id.btnPresent);
            btnAbsent  = itemView.findViewById(R.id.btnAbsent);
            btnLate    = itemView.findViewById(R.id.btnLate);
            btnExcused = itemView.findViewById(R.id.btnExcused);
        }
    }
}