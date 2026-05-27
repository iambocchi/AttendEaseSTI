package com.pumk.attendeasesti.Students.student_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.AttendanceHistoryModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentAttendanceHistoryAdapter
        extends RecyclerView.Adapter<StudentAttendanceHistoryAdapter.AttendanceViewHolder> {

    private List<AttendanceHistoryModel> list;

    // Tracks which positions are expanded
    private final Set<Integer> expandedPositions = new HashSet<>();

    public StudentAttendanceHistoryAdapter(List<AttendanceHistoryModel> list) {
        this.list = new ArrayList<>(list);
    }

    public void updateList(List<AttendanceHistoryModel> newList) {
        this.list = new ArrayList<>(newList);
        expandedPositions.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_myattendance_history_card, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceHistoryModel record = list.get(position);

        // Date info
        holder.month.setText(record.getMonth() != null ? record.getMonth() : "");
        holder.dayAndYear.setText(extractDayAndYear(record.getDate()));

        // Status badge
        applyStatusBadge(holder, record.getStatus());

        // Expanded subject row
        boolean isExpanded = expandedPositions.contains(position);
        holder.expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivDropdownArrow.setRotation(isExpanded ? 180f : 0f);

        // Subject text inside expanded section
        holder.subjectText.setText(record.getSubject() != null ? record.getSubject() : "N/A");
        holder.expandedStatus.setText(record.getStatus() != null ? record.getStatus() : "N/A");

        // Toggle expand/collapse on arrow click
        holder.ivDropdownArrow.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (expandedPositions.contains(pos)) {
                expandedPositions.remove(pos);
            } else {
                expandedPositions.add(pos);
            }
            notifyItemChanged(pos);
        });
    }

    /**
     * Extracts "27, 2026" from "May 27, 2026"
     */
    private String extractDayAndYear(String fullDate) {
        if (fullDate == null) return "";
        int spaceIndex = fullDate.indexOf(' ');
        if (spaceIndex >= 0 && spaceIndex < fullDate.length() - 1) {
            return fullDate.substring(spaceIndex + 1); // "27, 2026"
        }
        return fullDate;
    }

    private void applyStatusBadge(AttendanceViewHolder holder, String status) {
        if (status == null) status = "";
        switch (status.toLowerCase()) {
            case "present":
                holder.statusBadge.setText("✔ Present");
                holder.statusBadgeContainer.setBackgroundResource(R.drawable.bg_present);
                break;
            case "absent":
                holder.statusBadge.setText("✘ Absent");
                holder.statusBadgeContainer.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFFF5252));
                holder.statusBadge.setTextColor(0xFFFFFFFF);
                break;
            case "late":
                holder.statusBadge.setText("⏰ Late");
                holder.statusBadgeContainer.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFFFC107));
                holder.statusBadge.setTextColor(0xFF000000);
                break;
            case "excused":
                holder.statusBadge.setText("✉ Excused");
                holder.statusBadgeContainer.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF2196F3));
                holder.statusBadge.setTextColor(0xFFFFFFFF);
                break;
            default:
                holder.statusBadge.setText("No Record");
                holder.statusBadgeContainer.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFBDBDBD));
                holder.statusBadge.setTextColor(0xFFFFFFFF);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView    month, dayAndYear, statusBadge, subjectText, expandedStatus;
        ImageView   ivDropdownArrow;
        LinearLayout statusBadgeContainer, expandedLayout;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            month                = itemView.findViewById(R.id.month);
            dayAndYear           = itemView.findViewById(R.id.dayAndYear);
            statusBadge          = itemView.findViewById(R.id.ivStatusBadge);
            statusBadgeContainer = itemView.findViewById(R.id.layoutStatusBadge);
            ivDropdownArrow      = itemView.findViewById(R.id.ivDropdownArrow);
            expandedLayout       = itemView.findViewById(R.id.expandedLayout);   // add to card XML
            subjectText          = itemView.findViewById(R.id.tvSubjectExpanded); // add to card XML
            expandedStatus       = itemView.findViewById(R.id.tvStatusExpanded);  // add to card XML
        }
    }
}