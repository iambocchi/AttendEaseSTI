package com.pumk.attendeasesti.HeadOffices.headoffice_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.List;

public class HeadOfficeManagementAdapter extends RecyclerView.Adapter<HeadOfficeManagementAdapter.TeacherViewHolder> {

    public interface OnTeacherClickListener {
        void onTeacherClick(TeacherModel teacher);
    }

    private List<TeacherModel> teacherList;
    private OnTeacherClickListener listener;

    public HeadOfficeManagementAdapter(List<TeacherModel> teacherList, OnTeacherClickListener listener) {
        this.teacherList = teacherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.headoffice_teacher_management_card, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherModel teacher = teacherList.get(position);
        holder.bind(teacher, listener);
    }

    @Override
    public int getItemCount() {
        return teacherList != null ? teacherList.size() : 0;
    }

    public void updateList(List<TeacherModel> newList) {
        this.teacherList = newList;
        notifyDataSetChanged();
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name, email;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.teacher_profile_image);
            name = itemView.findViewById(R.id.teacher_name);
            email = itemView.findViewById(R.id.teacher_email);
        }

        public void bind(TeacherModel teacher, OnTeacherClickListener listener) {
            name.setText(teacher.getName());
            email.setText(teacher.getEmail());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onTeacherClick(teacher);
            });
        }
    }
}