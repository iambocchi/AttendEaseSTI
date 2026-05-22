package com.pumk.attendeasesti.Teachers.teacher_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.StudentModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherStudentAttendanceAdapter
        extends RecyclerView.Adapter<TeacherStudentAttendanceAdapter.StudentViewHolder>
        implements Filterable {

    private List<StudentModel> fullList;      // original unfiltered list
    private List<StudentModel> filteredList;  // shown in RecyclerView
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TeacherStudentAttendanceAdapter(List<StudentModel> list) {
        this.fullList     = new ArrayList<>(list);
        this.filteredList = new ArrayList<>(list);
    }

    // Called when Firestore snapshot arrives
    public void updateList(List<StudentModel> newList) {
        this.fullList     = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // ─── ViewHolder ────────────────────────────────────────────────────────────
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_student_attendance_card, parent, false); // your student card XML
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = filteredList.get(position);

        holder.name.setText(student.getName());
        holder.yearLevel.setText(student.getYear_level() != null ? student.getYear_level() : "");
        holder.program.setText(student.getProgram() != null ? student.getProgram() : "");

        // Set status image: bg_present if present, gray if absent/null
        boolean isPresent = "present".equalsIgnoreCase(student.getStatus());
        holder.statusImage.setImageResource(
                isPresent ? R.drawable.bg_present : R.drawable.gray
        );

        // Tap to toggle status and update Firestore immediately
        holder.statusImage.setOnClickListener(v -> {
            String newStatus = isPresent ? "absent" : "present";
            String studentEmail = student.getEmail();
            if (studentEmail == null || studentEmail.isEmpty()) return;

            // Update Firestore by querying the student's email
            db.collection("students")
                    .whereEqualTo("email", studentEmail)
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        if (!snapshots.isEmpty()) {
                            String docId = snapshots.getDocuments().get(0).getId();
                            db.collection("students")
                                    .document(docId)
                                    .update("status", newStatus)
                                    .addOnSuccessListener(unused -> {
                                        // Update local model and refresh this card immediately
                                        student.setStatus(newStatus);
                                        notifyItemChanged(holder.getAdapterPosition());
                                    });
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // ─── SearchView Filter (by name, section, year level) ─────────────────────
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
                        if (matchName || matchSection || matchYear) {
                            results.add(s);
                        }
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

    // ─── ViewHolder ────────────────────────────────────────────────────────────
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView  name, yearLevel, program;
        ImageView statusImage;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name        = itemView.findViewById(R.id.studentNameAttendance);
            yearLevel   = itemView.findViewById(R.id.yearlevelAttendance);
            program     = itemView.findViewById(R.id.programAttendance);
            statusImage = itemView.findViewById(R.id.status_present_absent);
        }
    }
}