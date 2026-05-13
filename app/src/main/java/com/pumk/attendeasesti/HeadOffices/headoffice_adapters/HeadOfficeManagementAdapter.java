package com.pumk.attendeasesti.HeadOffices.headoffice_adapters;

import com.pumk.attendeasesti.Teachers.TeacherModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumk.attendeasesti.R;

import java.util.List;

public class HeadOfficeManagementAdapter extends RecyclerView.Adapter<HeadOfficeManagementAdapter.HeadOfficeViewHolder> {

    private List<TeacherModel> teacherList;

    public HeadOfficeManagementAdapter(List<TeacherModel> teacherList) {
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public HeadOfficeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.headoffice_teacher_management_card, parent, false);

        return new HeadOfficeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadOfficeViewHolder holder, int position) {

        TeacherModel teacher = teacherList.get(position);

        holder.name.setText(teacher.getName());
        holder.email.setText(teacher.getEmail());
        holder.course.setText(teacher.getCourse());
        holder.status.setText(teacher.getStatus());

        holder.profileImage.setImageResource(teacher.getImageResId());
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class HeadOfficeViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView name, email, course, status;

        public HeadOfficeViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.teacher_profile_image);
            name = itemView.findViewById(R.id.teacher_name);
//            email = itemView.findViewById(R.id.teacher_email);
//
//            course = itemView.findViewById(R.id.teacher_course);
//            status = itemView.findViewById(R.id.teacher_status);
        }
    }
}