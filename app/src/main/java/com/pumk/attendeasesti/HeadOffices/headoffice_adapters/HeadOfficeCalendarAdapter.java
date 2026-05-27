package com.pumk.attendeasesti.HeadOffices.headoffice_adapters;

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
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HeadOfficeCalendarAdapter extends RecyclerView.Adapter<HeadOfficeCalendarAdapter.TeacherViewHolder>
        implements Filterable {

    private List<TeacherModel> fullList;
    private List<TeacherModel> filteredList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Cache toggled statuses so color survives scroll
    private final Map<String, String> updatedStatus = new HashMap<>();

    // Current selected date from fragment
    private String currentDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());

    public HeadOfficeCalendarAdapter(List<TeacherModel> teacherList) {
        this.fullList     = new ArrayList<>(teacherList);
        this.filteredList = new ArrayList<>(teacherList);
    }

    public void updateList(List<TeacherModel> newList) {
        fullList     = new ArrayList<>(newList);
        filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    /** Called by fragment when calendar date changes */
    public void setCurrentDate(String date) {
        this.currentDate = date;
        updatedStatus.clear(); // clear so colors reload fresh for new date
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.headoffice_calendar_card, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherModel teacher = filteredList.get(position);
        String email = teacher.getEmail();

        holder.name.setText(teacher.getName()         != null ? teacher.getName()       : "N/A");
        holder.email.setText(teacher.getEmail()       != null ? teacher.getEmail()      : "N/A");
        holder.course.setText(teacher.getDepartment() != null ? teacher.getDepartment() : "N/A");

        // Use cached status if available, else use status loaded from Firestore
        String status = updatedStatus.containsKey(email)
                ? updatedStatus.get(email)
                : teacher.getStatus();

        applyStatusButton(holder, status);

        // Editable — toggle Present/Absent on click, save to Firestore
        holder.statusBtn.setOnClickListener(v -> {
            String current = updatedStatus.containsKey(email)
                    ? updatedStatus.get(email)
                    : teacher.getStatus();

            String newStatus = "Present".equalsIgnoreCase(current) ? "Absent" : "Present";
            updatedStatus.put(email, newStatus);
            applyStatusButton(holder, newStatus);
            saveOrUpdateAttendance(teacher, newStatus);
        });
    }

    private void applyStatusButton(TeacherViewHolder holder, String status) {
        if ("Present".equalsIgnoreCase(status)) {
            holder.statusBtn.setText("Present");
            holder.statusBtn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF80EF80)); // green
        } else if ("Absent".equalsIgnoreCase(status)) {
            holder.statusBtn.setText("Absent");
            holder.statusBtn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFFF5252)); // red
        } else {
            // "No Record" — gray, first click will create the document
            holder.statusBtn.setText("No Record");
            holder.statusBtn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFBDBDBD)); // gray
        }
    }

    /**
     * Creates or updates teachers/{docId}/Attendance/{currentDate}
     * Fields: date, dayofweek, month, status — matches your Firestore structure exactly.
     * Uses set() so it creates the document if it doesn't exist yet.
     */
    private void saveOrUpdateAttendance(TeacherModel teacher, String newStatus) {
        String email = teacher.getEmail();
        if (email == null || email.isEmpty()) return;

        db.collection("teachers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) return;

                    String teacherDocId = snapshots.getDocuments().get(0).getId();

                    String dayOfWeek = "";
                    String month     = "";
                    try {
                        Date parsed = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).parse(currentDate);
                        dayOfWeek = new SimpleDateFormat("EEE", Locale.getDefault()).format(parsed);   // "Wed"
                        month     = new SimpleDateFormat("MMMM", Locale.getDefault()).format(parsed);  // "May"
                    } catch (Exception ignored) {}

                    Map<String, Object> attendanceData = new HashMap<>();
                    attendanceData.put("date",      currentDate); // "May 27, 2026"
                    attendanceData.put("dayofweek", dayOfWeek);   // "Wed"
                    attendanceData.put("month",     month);       // "May"
                    attendanceData.put("status",    newStatus);   // "Present" / "Absent"

                    // set() creates the doc if new day, updates if already exists
                    db.collection("teachers")
                            .document(teacherDocId)
                            .collection("Attendance")
                            .document(currentDate)
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
                List<TeacherModel> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(fullList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (TeacherModel t : fullList) {
                        boolean matchName  = t.getName()       != null && t.getName().toLowerCase().contains(query);
                        boolean matchEmail = t.getEmail()      != null && t.getEmail().toLowerCase().contains(query);
                        boolean matchDept  = t.getDepartment() != null && t.getDepartment().toLowerCase().contains(query);
                        if (matchName || matchEmail || matchDept) results.add(t);
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
        TextView name, email, course;
        Button statusBtn;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            name      = itemView.findViewById(R.id.name);
            email     = itemView.findViewById(R.id.email);
            course    = itemView.findViewById(R.id.course);
            statusBtn = itemView.findViewById(R.id.statusBtn);
        }
    }
}