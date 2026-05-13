//package com.pumk.attendeasesti.Students.student_fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.pumk.attendeasesti.R;
//import com.pumk.attendeasesti.Teachers.StudentAdapter;
//import com.pumk.attendeasesti.Teachers.TeacherModel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TeacherFragment extends Fragment {
//
//    RecyclerView recyclerView;
//    StudentAdapter adapter;
//    List<TeacherModel> teacherList;
//    SearchView searchView;
//
//    public TeacherFragment() {
//        // Required empty constructor
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
//
//        recyclerView = view.findViewById(R.id.recyclerView);
//        searchView = view.findViewById(R.id.searchView);
//
//        teacherList = new ArrayList<>();
//
//        // SAMPLE DATA
//        teacherList.add(new TeacherModel(
//                "Sarah Geronimo",
//                "sarah@school.edu",
//                "Computer Science",
//                "Success",
//                R.drawable.ic_avatar_placeholder
//        ));
//
//        teacherList.add(new TeacherModel(
//                "John Doe",
//                "john@school.edu",
//                "Mathematics",
//                "Active",
//                R.drawable.ic_avatar_placeholder
//        ));
//
//        adapter = new StudentAdapter(requireContext(), teacherList);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        recyclerView.setAdapter(adapter);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//
//        return view;
//    }
//}
