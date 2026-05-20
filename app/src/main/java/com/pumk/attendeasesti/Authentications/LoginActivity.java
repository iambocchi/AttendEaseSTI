package com.pumk.attendeasesti.Authentications;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pumk.attendeasesti.HeadOffices.HeadOfficeActivity;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.StudentActivity;
import com.pumk.attendeasesti.Teachers.TeacherActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    TextInputEditText etEmail, etPassword;
    TextInputLayout emailContainer, passwordContainer;
    AutoCompleteTextView autoCompleteTextView;
    Button loginbtn;
    TextView forgotpassword;

    private String selectedItem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        // Status bar color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        emailContainer = findViewById(R.id.emailContainer);
        passwordContainer = findViewById(R.id.passwordContainer);
        autoCompleteTextView = findViewById(R.id.userType);
        loginbtn = findViewById(R.id.btnLogin);
        forgotpassword = findViewById(R.id.tvForgotPassword);

        // Dropdown Roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles,
                R.layout.spinner_selected_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText("Choose your role", false);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) ->
                selectedItem = parent.getItemAtPosition(position).toString()
        );

        loginbtn.setOnClickListener(v -> login());

        forgotpassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        emailContainer.setError(null);
        passwordContainer.setError(null);

        // Validation
        if (email.isEmpty()) {
            emailContainer.setError("Email required");
            return;
        }

        if (password.isEmpty()) {
            passwordContainer.setError("Password required");
            return;
        }

        if (selectedItem.isEmpty() || selectedItem.equals("Choose your role")) {
            Toast.makeText(this, "Please select a role first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double clicks
        loginbtn.setEnabled(false);

        // Step 1: Firebase Auth sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Step 2: Check Firestore collection based on selected role
                        verifyUserRoleInFirestore(email);
                    } else {
                        loginbtn.setEnabled(true);
                        Toast.makeText(
                                LoginActivity.this,
                                "Authentication Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void verifyUserRoleInFirestore(String email) {
        // Map selected role to Firestore collection name
        String collection;

        switch (selectedItem) {
            case "Student":
                collection = "students";
                break;
            case "Teacher":
                collection = "teachers";
                break;
            case "Head Office":
                collection = "headoffice";
                break;
            default:
                loginbtn.setEnabled(true);
                Toast.makeText(this, "Invalid role selected", Toast.LENGTH_SHORT).show();
                return;
        }

        // Query Firestore: find a document where email == entered email
        db.collection(collection)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {

                    loginbtn.setEnabled(true);

                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();

                        if (snapshot != null && !snapshot.isEmpty()) {
                            // User found in the correct collection — proceed
                            navigateToHome();
                        } else {
                            // Signed in with Firebase Auth but NOT in this role's collection
                            mAuth.signOut();
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Access denied. Your account is not registered as a " + selectedItem + ".",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } else {
                        mAuth.signOut();
                        Toast.makeText(
                                LoginActivity.this,
                                "Failed to verify role: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent;

        switch (selectedItem) {
            case "Student":
                intent = new Intent(LoginActivity.this, StudentActivity.class);
                break;
            case "Teacher":
                intent = new Intent(LoginActivity.this, TeacherActivity.class);
                break;
            case "Head Office":
                intent = new Intent(LoginActivity.this, HeadOfficeActivity.class);
                break;
            default:
                return;
        }

        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}