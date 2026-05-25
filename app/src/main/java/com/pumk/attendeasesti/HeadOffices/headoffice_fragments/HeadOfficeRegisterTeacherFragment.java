package com.pumk.attendeasesti.HeadOffices.headoffice_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pumk.attendeasesti.R;

import java.util.HashMap;
import java.util.Map;

public class HeadOfficeRegisterTeacherFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText nameInput, emailInput, passwordInput;
    private TextInputLayout nameContainer, emailContainer, passwordContainer;
    private AutoCompleteTextView departmentDropdown, campusDropdown;
    private Button addSchedBtn, saveBtn, cancelBtn;

    private String selectedDepartment = "";
    private String selectedCampus = "";

    public HeadOfficeRegisterTeacherFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.headoffice_registerteacher,
                container,
                false
        );

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // Initialize Views
        nameInput          = view.findViewById(R.id.name_input);
        emailInput         = view.findViewById(R.id.email_input);
        passwordInput      = view.findViewById(R.id.password_input);
        departmentDropdown = view.findViewById(R.id.departmentDropdown);
        campusDropdown     = view.findViewById(R.id.campusDropdown);
        addSchedBtn        = view.findViewById(R.id.regisAddSched);
        saveBtn            = view.findViewById(R.id.regisSave);
        cancelBtn          = view.findViewById(R.id.regisCancel);

        // Resolve TextInputLayouts from their child EditTexts
        nameContainer     = (TextInputLayout) nameInput.getParent().getParent();
        emailContainer    = (TextInputLayout) emailInput.getParent().getParent();
        passwordContainer = (TextInputLayout) passwordInput.getParent().getParent();

        // Department dropdown
        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.departments,
                R.layout.spinner_selected_item
        );
        deptAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        departmentDropdown.setAdapter(deptAdapter);
        departmentDropdown.setText("Choose your department", false);
        departmentDropdown.setOnItemClickListener((parent, v, position, id) ->
                selectedDepartment = parent.getItemAtPosition(position).toString()
        );

        // Campus dropdown
        ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.campuses,
                R.layout.spinner_selected_item
        );
        campusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        campusDropdown.setAdapter(campusAdapter);
        campusDropdown.setText("Choose your campus", false);
        campusDropdown.setOnItemClickListener((parent, v, position, id) ->
                selectedCampus = parent.getItemAtPosition(position).toString()
        );

        // Add Schedule button
        addSchedBtn.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Schedule picker coming soon", Toast.LENGTH_SHORT).show()
        );

        // Save button
        saveBtn.setOnClickListener(v -> registerTeacher());

        // Cancel button — go back to previous fragment
        cancelBtn.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        return view;
    }

    private void registerTeacher() {
        String name     = nameInput.getText().toString().trim();
        String email    = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Clear previous errors
        nameContainer.setError(null);
        emailContainer.setError(null);
        passwordContainer.setError(null);

        // Validation
        if (name.isEmpty()) {
            nameContainer.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            emailContainer.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            passwordContainer.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordContainer.setError("Password must be at least 6 characters");
            return;
        }

        if (selectedDepartment.isEmpty() || selectedDepartment.equals("Choose your department")) {
            Toast.makeText(requireContext(), "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCampus.isEmpty() || selectedCampus.equals("Choose your campus")) {
            Toast.makeText(requireContext(), "Please select a campus", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Create Firebase Auth account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        generateTeacherIdAndSave(uid, name, email);
                    } else {
                        Toast.makeText(
                                requireContext(),
                                "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    /**
     * Transaction on meta/teacherCounter to safely auto-increment teacher_id.
     * First teacher = 100, then 101, 102, etc.
     */
    private void generateTeacherIdAndSave(String uid, String name, String email) {
        DocumentReference counterRef = db.collection("meta").document("teacherCounter");

        db.runTransaction(transaction -> {
            Long current = 99L;
            try {
                Long stored = transaction.get(counterRef).getLong("lastId");
                if (stored != null) current = stored;
            } catch (Exception ignored) {}

            long newId = current + 1;
            Map<String, Object> counterUpdate = new HashMap<>();
            counterUpdate.put("lastId", newId);
            transaction.set(counterRef, counterUpdate);
            return newId;

        }).addOnSuccessListener(newId ->
                saveTeacherToFirestore(uid, name, email, newId.intValue())

        ).addOnFailureListener(e ->
                Toast.makeText(
                        requireContext(),
                        "Could not generate teacher ID: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    private void saveTeacherToFirestore(String uid, String name, String email, int teacherId) {
        Map<String, Object> teacherData = new HashMap<>();
        teacherData.put("uid",        uid);
        teacherData.put("name",       name);
        teacherData.put("email",      email);
        teacherData.put("department", selectedDepartment);
        teacherData.put("campus",     selectedCampus);
        teacherData.put("teacher_id", teacherId);
        teacherData.put("role",       "Teacher");
        teacherData.put("status",     "Present"); // default status

        db.collection("teachers")
                .document(uid)
                .set(teacherData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(
                            requireContext(),
                            "Teacher registered successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                    clearForm();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                requireContext(),
                                "Firestore Error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // Reset form fields after successful registration
    private void clearForm() {
        nameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        departmentDropdown.setText("Choose your department", false);
        campusDropdown.setText("Choose your campus", false);
        selectedDepartment = "";
        selectedCampus = "";
    }
}