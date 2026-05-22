package com.pumk.attendeasesti.Teachers.teacher_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.StudentModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherAbsentRequestAdapter extends RecyclerView.Adapter<TeacherAbsentRequestAdapter.StudentViewHolder>
        implements Filterable {

    private List<StudentModel> fullList;
    private List<StudentModel> filteredList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TeacherAbsentRequestAdapter(List<StudentModel> studentList) {
        this.fullList     = new ArrayList<>(studentList);
        this.filteredList = new ArrayList<>(studentList);
    }

    public void updateList(List<StudentModel> newList) {
        fullList     = new ArrayList<>(newList);
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_absence_request_card, parent, false); // your card XML
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = filteredList.get(position);

        holder.name.setText(student.getName()       != null ? student.getName()    : "N/A");
        holder.email.setText(student.getEmail()     != null ? student.getEmail()   : "N/A");
        holder.program.setText(student.getProgram() != null ? student.getProgram() : "N/A");

        // Always "Mark Present" since this list only shows absent students
        holder.statusBtn.setText("Mark Present");
        holder.statusBtn.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFFF5252));

        holder.statusBtn.setOnClickListener(v -> {
            String email = student.getEmail();
            if (email == null || email.isEmpty()) return;

            db.collection("students")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        if (snapshots.isEmpty()) return;

                        String docId = snapshots.getDocuments().get(0).getId();
                        db.collection("students")
                                .document(docId)
                                .update("status", "present")
                                .addOnSuccessListener(unused -> {
                                    // Remove card immediately after marking present
                                    int pos = holder.getAdapterPosition();
                                    if (pos != RecyclerView.NO_ID) {
                                        filteredList.remove(pos);
                                        fullList.remove(student);
                                        notifyItemRemoved(pos);
                                    }
                                });
                    });
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
                        boolean matchName    = s.getName()    != null && s.getName().toLowerCase().contains(query);
                        boolean matchEmail   = s.getEmail()   != null && s.getEmail().toLowerCase().contains(query);
                        boolean matchProgram = s.getProgram() != null && s.getProgram().toLowerCase().contains(query);
                        if (matchName || matchEmail || matchProgram) results.add(s);
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
        TextView name, email, program;
        Button statusBtn;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name      = itemView.findViewById(R.id.name);
            email     = itemView.findViewById(R.id.email);
            program   = itemView.findViewById(R.id.program);
            statusBtn = itemView.findViewById(R.id.statusBtn);
        }
    }
}