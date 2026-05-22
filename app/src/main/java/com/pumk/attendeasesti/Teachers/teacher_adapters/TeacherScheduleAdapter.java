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

public class TeacherScheduleAdapter extends RecyclerView.Adapter<TeacherScheduleAdapter.SubjectViewHolder> {

    private List<SubjectModel> subjectList;

    public TeacherScheduleAdapter(List<SubjectModel> subjectList) {
        this.subjectList = new ArrayList<>(subjectList);
    }

    public void updateList(List<SubjectModel> newList) {
        this.subjectList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_my_schedule_card, parent, false); // your card XML
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectModel subject = subjectList.get(position);
        holder.subjectName.setText(subject.getSubject_name());
        holder.subjectDay.setText(subject.getDay());
        holder.subjectTime.setText(subject.getTime());
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, subjectDay, subjectTime;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subject_name);
            subjectDay  = itemView.findViewById(R.id.subjectDay);
            subjectTime = itemView.findViewById(R.id.subject_time);
        }
    }
}