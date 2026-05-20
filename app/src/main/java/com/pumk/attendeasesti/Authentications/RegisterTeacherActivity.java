package com.pumk.attendeasesti.Authentications;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pumk.attendeasesti.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterTeacherActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText nameInput, emailInput, passwordInput;
    private TextInputLayout nameContainer, emailContainer, passwordContainer;
    private AutoCompleteTextView departmentDropdown, campusDropdown;
    private Button addSchedBtn, saveBtn, cancelBtn;

    private String selectedDepartment = "";
    private String selectedCampus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headoffice_registerteacher);

        // Status bar color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // Initialize Views
        nameInput          = findViewById(R.id.name_input);
        emailInput         = findViewById(R.id.email_input);
        passwordInput      = findViewById(R.id.password_input);
        departmentDropdown = findViewById(R.id.departmentDropdown);
        campusDropdown     = findViewById(R.id.campusDropdown);
        addSchedBtn        = findViewById(R.id.regisAddSched);
        saveBtn            = findViewById(R.id.regisSave);
        cancelBtn          = findViewById(R.id.regisCancel);

        // Resolve TextInputLayouts
        nameContainer     = (TextInputLayout) nameInput.getParent().getParent();
        emailContainer    = (TextInputLayout) emailInput.getParent().getParent();
        passwordContainer = (TextInputLayout) passwordInput.getParent().getParent();

        // Department dropdown — same pattern as login's role dropdown
        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.departments,
                R.layout.spinner_selected_item        // reuse your existing spinner layout
        );
        deptAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        departmentDropdown.setAdapter(deptAdapter);
        departmentDropdown.setText("Choose your department", false);
        departmentDropdown.setOnItemClickListener((parent, view, position, id) ->
                selectedDepartment = parent.getItemAtPosition(position).toString()
        );

        // Campus dropdown — same pattern
        ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.campuses,                     // add this in res/values/arrays.xml
                R.layout.spinner_selected_item
        );
        campusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        campusDropdown.setAdapter(campusAdapter);
        campusDropdown.setText("Choose your campus", false);
        campusDropdown.setOnItemClickListener((parent, view, position, id) ->
                selectedCampus = parent.getItemAtPosition(position).toString()
        );

        // Add Schedule button
        addSchedBtn.setOnClickListener(v ->
                Toast.makeText(this, "Schedule picker coming soon", Toast.LENGTH_SHORT).show()
        );

        // Save
        saveBtn.setOnClickListener(v -> registerTeacher());

        // Cancel
        cancelBtn.setOnClickListener(v -> finish());
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
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCampus.isEmpty() || selectedCampus.equals("Choose your campus")) {
            Toast.makeText(this, "Please select a campus", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Create Firebase Auth account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        generateTeacherIdAndSave(uid, name, email);
                    } else {
                        Toast.makeText(
                                RegisterTeacherActivity.this,
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
            } catch (Exception ignored) { }

            long newId = current + 1;
            Map<String, Object> counterUpdate = new HashMap<>();
            counterUpdate.put("lastId", newId);
            transaction.set(counterRef, counterUpdate);
            return newId;

        }).addOnSuccessListener(newId ->
                saveTeacherToFirestore(uid, name, email, newId.intValue())

        ).addOnFailureListener(e ->
                Toast.makeText(
                        RegisterTeacherActivity.this,
                        "Could not generate teacher ID: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    private void saveTeacherToFirestore(String uid, String name, String email, int teacherId) {
        // Field names match TeacherModel exactly
        Map<String, Object> teacherData = new HashMap<>();
        teacherData.put("uid",        uid);
        teacherData.put("name",       name);
        teacherData.put("email",      email);
        teacherData.put("department", selectedDepartment);
        teacherData.put("campus",     selectedCampus);
        teacherData.put("teacher_id", teacherId);
        teacherData.put("role",       "Teacher");

        db.collection("teachers")
                .document(uid)
                .set(teacherData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(
                            RegisterTeacherActivity.this,
                            "Teacher registered successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                    goToConfirmation(name, email, teacherId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                RegisterTeacherActivity.this,
                                "Firestore Error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void goToConfirmation(String name, String email, int teacherId) {
        Intent intent = new Intent(RegisterTeacherActivity.this, RegisterTeacherActivity.class);
        intent.putExtra("name",       name);
        intent.putExtra("email",      email);
        intent.putExtra("department", selectedDepartment);
        intent.putExtra("campus",     selectedCampus);
        intent.putExtra("teacher_id", teacherId);
        startActivity(intent);
        finish();
    }
}