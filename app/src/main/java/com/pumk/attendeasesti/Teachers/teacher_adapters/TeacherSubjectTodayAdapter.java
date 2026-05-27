package com.pumk.attendeasesti.Teachers.teacher_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.SubjectModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherSubjectTodayAdapter extends RecyclerView.Adapter<TeacherSubjectTodayAdapter.SubjectViewHolder> {

    private List<SubjectModel> subjectList;

    public TeacherSubjectTodayAdapter(List<SubjectModel> list) {
        this.subjectList = new ArrayList<>(list);
    }

    public void updateList(List<SubjectModel> newList) {
        this.subjectList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_student_attendance_subject_card, parent, false); // your subject info card XML
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectModel subject = subjectList.get(position);
        holder.subjectName.setText(subject.getSubject_name() != null ? subject.getSubject_name() : "N/A");
        holder.day.setText(subject.getDay() != null ? subject.getDay() : "N/A");
        holder.time.setText(subject.getTime() != null ? subject.getTime() : "N/A");
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, day, time;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subject_name);
            day         = itemView.findViewById(R.id.day);
            time        = itemView.findViewById(R.id.time);
        }
    }
}