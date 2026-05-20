package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.pumk.attendeasesti.R;

public class HeadOfficeRegisterTeacherFragment extends Fragment {

    private AutoCompleteTextView departmentDropdown, campusDropdown;

    private String selectedDepartment = "";
    private String selectedCampus = "";

    public HeadOfficeRegisterTeacherFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Inflate FIRST
        View view = inflater.inflate(
                R.layout.headoffice_registerteacher,
                container,
                false
        );

        // Find views
        departmentDropdown = view.findViewById(R.id.departmentDropdown);
        campusDropdown = view.findViewById(R.id.campusDropdown);

        // Department dropdown
        ArrayAdapter<CharSequence> deptAdapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.departments,
                        R.layout.spinner_selected_item
                );

        deptAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        departmentDropdown.setAdapter(deptAdapter);
        departmentDropdown.setText(
                "Choose your department",
                false
        );

        departmentDropdown.setOnItemClickListener(
                (parent, v, position, id) ->
                        selectedDepartment =
                                parent.getItemAtPosition(position).toString()
        );






        // Campus dropdown
        ArrayAdapter<CharSequence> campusAdapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.campuses,
                        R.layout.spinner_selected_item
                );

        campusAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        campusDropdown.setAdapter(campusAdapter);
        campusDropdown.setText(
                "Choose your campus",
                false
        );

        campusDropdown.setOnItemClickListener(
                (parent, v, position, id) ->
                        selectedCampus =
                                parent.getItemAtPosition(position).toString()
        );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(
                requireContext(),
                "IRUN",
                Toast.LENGTH_SHORT
        ).show();
    }
}