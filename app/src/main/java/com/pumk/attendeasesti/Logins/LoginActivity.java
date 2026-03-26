package com.pumk.attendeasesti.Logins;

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
import com.pumk.attendeasesti.HeadOffices.HeadOfficeActivity;
import com.pumk.attendeasesti.R;
import com.pumk.attendeasesti.Students.StudentActivity;
import com.pumk.attendeasesti.Teachers.TeacherActivity;

public class LoginActivity extends AppCompatActivity {

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

//        AT THE VERY TOP COLOR
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        }

        // ✅ Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        emailContainer = findViewById(R.id.emailContainer);
        passwordContainer = findViewById(R.id.passwordContainer);
        autoCompleteTextView = findViewById(R.id.userType);
        loginbtn = findViewById(R.id.btnLogin);
        forgotpassword = findViewById(R.id.tvForgotPassword);

        // ✅ Dropdown (roles)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles,
                R.layout.spinner_selected_item
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        autoCompleteTextView.setAdapter(adapter);

        // ✅ Fix default text
        autoCompleteTextView.setText("Choose your role", false);

        // ✅ Get selected role
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = parent.getItemAtPosition(position).toString();
        });

        // ✅ Login button
        loginbtn.setOnClickListener(v -> login());

        // ✅ Forgot password click
        forgotpassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void login() { if (selectedItem.equals("") || selectedItem.equals("Choose your role")) { Toast.makeText(this, "Please select a role first", Toast.LENGTH_SHORT).show(); return; } Intent intent; switch (selectedItem) { case "Student": intent = new Intent(this, StudentActivity.class); break; case "Teacher": intent = new Intent(this, TeacherActivity.class); break; case "Head Office": intent = new Intent(this, HeadOfficeActivity.class); break; default: Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show(); return; } startActivity(intent); }

//NEW LOGIN LOGIC
//    private void login() {
//
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//
//        // ✅ Reset errors
//        emailContainer.setError(null);
//        passwordContainer.setError(null);
//
//        // ✅ Validate email
//        if (email.isEmpty()) {
//            emailContainer.setError("Email required");
//            return;
//        }
//
//        // ✅ Validate password
//        if (password.isEmpty()) {
//            passwordContainer.setError("Password required");
//            return;
//        }
//
//        // ✅ Validate role
//        if (selectedItem.equals("") || selectedItem.equals("Choose your role")) {
//            Toast.makeText(this, "Please select a role first", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Intent intent;
//
//        // 🔐 DEMO LOGIN (role-based)
//        switch (selectedItem) {
//
//            case "Student":
//                if (email.equals("student@gmail.com") && password.equals("1234")) {
//                    intent = new Intent(this, StudentActivity.class);
//                } else {
//                    Toast.makeText(this, "Invalid Student credentials", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                break;
//
//            case "Teacher":
//                if (email.equals("teacher@gmail.com") && password.equals("1234")) {
//                    intent = new Intent(this, TeacherActivity.class);
//                } else {
//                    Toast.makeText(this, "Invalid Teacher credentials", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                break;
//
//            case "Head Office":
//                if (email.equals("admin@gmail.com") && password.equals("1234")) {
//                    intent = new Intent(this, HeadOfficeActivity.class);
//                } else {
//                    Toast.makeText(this, "Invalid Admin credentials", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                break;
//
//            default:
//                Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show();
//                return;
//        }
//
//        // ✅ Go to selected activity
//        startActivity(intent);
//    }
}