package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.HeadOffices.headoffice_adapters.HeadOfficeManagementAdapter;
import com.pumk.attendeasesti.Teachers.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class HeadOfficeTeacherManagementFragment extends Fragment {

    RecyclerView recyclerView;
    HeadOfficeManagementAdapter adapter;
    List<TeacherModel> teacherList;

    public HeadOfficeTeacherManagementFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.headoffice_teacher_management,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerViewTeachers);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        teacherList = new ArrayList<>();

        // Sample data
        teacherList.add(new TeacherModel(
                "Sarah Johnson",
                "sarah.johnson@school.edu",
                "BSIT",
                "Active",
                R.drawable.ic_avatar_placeholder
        ));

        teacherList.add(new TeacherModel(
                "John Smith",
                "john.smith@school.edu",
                "BSCS",
                "Inactive",
                R.drawable.ic_avatar_placeholder
        ));

        teacherList.add(new TeacherModel(
                "Emily Davis",
                "emily.davis@school.edu",
                "BSIT",
                "Active",
                R.drawable.ic_avatar_placeholder
        ));

        adapter = new HeadOfficeManagementAdapter(teacherList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}