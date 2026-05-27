package com.pumk.attendeasesti.HeadOffices.headoffice_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class HeadOfficeTeacherAttendanceAdapter extends RecyclerView.Adapter<HeadOfficeTeacherAttendanceAdapter.TeacherViewHolder>
        implements Filterable {

    private List<TeacherModel> fullList;
    private List<TeacherModel> filteredList;

    public HeadOfficeTeacherAttendanceAdapter(List<TeacherModel> teacherList) {
        this.fullList     = new ArrayList<>(teacherList);
        this.filteredList = new ArrayList<>(teacherList);
    }

    public void updateList(List<TeacherModel> newList) {
        fullList     = new ArrayList<>(newList);
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.headoffice_teacher_attendance_card, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherModel teacher = filteredList.get(position);

        holder.name.setText(teacher.getName()         != null ? teacher.getName()       : "N/A");
        holder.email.setText(teacher.getEmail()       != null ? teacher.getEmail()      : "N/A");
        holder.department.setText(teacher.getDepartment() != null ? teacher.getDepartment() : "N/A");

        boolean isPresent = "Present".equalsIgnoreCase(teacher.getStatus());

        if (isPresent) {
            holder.statusBtn.setText("Present");
            holder.statusBtn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF00E676)); // green
        } else {
            holder.statusBtn.setText("Absent");
            holder.statusBtn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFFF5252)); // red
        }

        // Read-only — no click listener, no interaction
        holder.statusBtn.setClickable(false);
        holder.statusBtn.setFocusable(false);
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
                List<TeacherModel> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(fullList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (TeacherModel t : fullList) {
                        if (t.getName()       != null && t.getName().toLowerCase().contains(query)
                                || t.getEmail()      != null && t.getEmail().toLowerCase().contains(query)
                                || t.getDepartment() != null && t.getDepartment().toLowerCase().contains(query)
                                || t.getCampus()     != null && t.getCampus().toLowerCase().contains(query)) {
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