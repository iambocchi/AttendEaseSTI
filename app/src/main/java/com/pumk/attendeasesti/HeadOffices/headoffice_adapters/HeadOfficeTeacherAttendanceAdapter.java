package com.pumk.attendeasesti.HeadOffices.headoffice_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.material.button.MaterialButton;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class HeadOfficeTeacherAttendanceAdapter extends RecyclerView.Adapter<HeadOfficeTeacherAttendanceAdapter.TeacherViewHolder>
        implements Filterable {

    private List<TeacherModel> fullList;      // original unfiltered list
    private List<TeacherModel> filteredList;  // what the RecyclerView actually shows

    public HeadOfficeTeacherAttendanceAdapter(List<TeacherModel> teacherList) {
        this.fullList     = new ArrayList<>(teacherList);
        this.filteredList = new ArrayList<>(teacherList);
    }

    // ─── Update data when Firestore snapshot arrives ───────────────────────────
    public void updateList(List<TeacherModel> newList) {
        fullList     = new ArrayList<>(newList);
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // ─── ViewHolder ────────────────────────────────────────────────────────────
    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.headoffice_teacher_attendance_card, parent, false); // your card XML file name
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherModel teacher = filteredList.get(position);

        holder.name.setText(teacher.getName());
        holder.email.setText(teacher.getEmail());
        holder.department.setText(teacher.getDepartment());

        String currentStatus = teacher.getStatus();
        // Check if present (handles null and case sensitivity)
        boolean isPresent = "Present".equalsIgnoreCase(currentStatus);

        // --- UI Setup ---
        if (isPresent) {
            holder.statusBtn.setText("Present");
            holder.statusBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00E676)); // Green
        } else {
            holder.statusBtn.setText("Absent");
            holder.statusBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF5252)); // Red
        }

        // --- Click Logic ---
        holder.statusBtn.setOnClickListener(v -> {
            // 1. Toggle the value
            String newStatus = isPresent ? "Absent" : "Present";

            // 2. IMPORTANT: Use the email from the TEACHER object, not the current Auth user
            String teacherEmail = teacher.getEmail();

            if (teacherEmail == null || teacherEmail.isEmpty()) return;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 3. Update Firestore
            db.collection("teachers")
                    .whereEqualTo("email", teacherEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            db.collection("teachers").document(docId)
                                    .update("status", newStatus)
                                    .addOnFailureListener(e -> {
                                        // Optional: Handle error (e.g. Toast)
                                    });
                        }
                    });
        });
    }
    @Override
    public int getItemCount() {
        return filteredList.size();
    }
//    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    FirebaseUser currentUser = mAuth.getCurrentUser();
//
//            if (currentUser == null) return;
//
//    String email = currentUser.getEmail();


    // ─── SearchView Filter ─────────────────────────────────────────────────────
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<TeacherModel> results = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.addAll(fullList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (TeacherModel t : fullList) {
                        if (t.getName().toLowerCase().contains(query)
                                || t.getEmail().toLowerCase().contains(query)
                                || t.getDepartment().toLowerCase().contains(query)
                                || t.getCampus().toLowerCase().contains(query)) {
                            results.add(t);
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
                filteredList = (List<TeacherModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    // ─── ViewHolder class ──────────────────────────────────────────────────────
    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, department;
        MaterialButton statusBtn;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            name       = itemView.findViewById(R.id.name);
            email      = itemView.findViewById(R.id.email);
            department = itemView.findViewById(R.id.sub);
            statusBtn  = itemView.findViewById(R.id.statusBtn);
        }
    }
}