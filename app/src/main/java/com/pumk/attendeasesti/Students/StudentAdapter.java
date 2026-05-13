package com.pumk.attendeasesti.Teachers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumk.attendeasesti.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder>
        implements Filterable {

    Context context;
    List<TeacherModel> teacherList;
    List<TeacherModel> teacherListFull;

    public StudentAdapter(Context context, List<TeacherModel> teacherList) {
        this.context = context;
        this.teacherList = teacherList;
        this.teacherListFull = new ArrayList<>(teacherList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.student_view_teacher_today, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TeacherModel model = teacherList.get(position);

        holder.profName.setText(model.getName());
        holder.profEmail.setText(model.getEmail());
        holder.profCourse.setText(model.getCourse());
        holder.statusBtn.setText(model.getStatus());
        holder.teachImage.setImageResource(model.getImageResId());
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView teachImage;
        TextView profName, profEmail, profCourse;
        Button statusBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            teachImage = itemView.findViewById(R.id.teachImage);
            profName = itemView.findViewById(R.id.profName);
            profEmail = itemView.findViewById(R.id.profEmail);
            profCourse = itemView.findViewById(R.id.profCourse);
            statusBtn = itemView.findViewById(R.id.statusBtn);
        }
    }

    // SEARCH FILTER
    @Override
    public Filter getFilter() {
        return teacherFilter;
    }

    private final Filter teacherFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<TeacherModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(teacherListFull);
            } else {

                String filterPattern = constraint.toString()
                        .toLowerCase(Locale.ROOT)
                        .trim();

                for (TeacherModel item : teacherListFull) {

                    if (item.getName().toLowerCase(Locale.ROOT).contains(filterPattern)
                            || item.getCourse().toLowerCase(Locale.ROOT).contains(filterPattern)
                            || item.getEmail().toLowerCase(Locale.ROOT).contains(filterPattern)) {

                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            teacherList.clear();
            teacherList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}