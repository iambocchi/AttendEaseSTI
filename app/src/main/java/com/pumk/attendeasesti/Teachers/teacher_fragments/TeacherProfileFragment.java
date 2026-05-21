package com.pumk.attendeasesti.Teachers.teacher_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pumk.attendeasesti.R;

public class TeacherProfileFragment extends Fragment {

    TextView nametext;
    TextView campustext;
    TextView departmenttext;
    TextView idtext;
    TextView emailtext;

    public TeacherProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Connect fragment to XML
        View view = inflater.inflate(
                R.layout.teacher_profile,
                container,
                false
        );

        nametext = view.findViewById(R.id.teacher_name);
        campustext = view.findViewById(R.id.teacher_campus);
        departmenttext = view.findViewById(R.id.teacher_department);
        idtext = view.findViewById(R.id.teacher_id);
        emailtext = view.findViewById(R.id.teacher_email);

        getCurrentUserData(); // ← inflateProfile() is now called INSIDE this, after data arrives
        return view;
    }
    private void getCurrentUserData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) return;

        String email = currentUser.getEmail();

        db.collection("teachers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        // ✅ No "String" keyword — assigns to class fields, not local variables
                        String name   = doc.getString("name");
                        String campus = doc.getString("campus");
                        String department = doc.getString("department");
                        Long idLong   = doc.getLong("id"); // id is a number in Firestore
                        String id     = idLong != null ? String.valueOf(idLong) : "";

                        // ✅ Called HERE, inside the callback, after data is ready
                        inflateProfile(name, campus,department, id, email);

                    } else {
                        Toast.makeText(
                                requireContext(),
                                "No user document found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("UserData", "Error fetching user: " + e.getMessage())
                );
    }

    private void inflateProfile(String name, String campus, String department, String id, String email) {
        nametext.setText(name != null ? name : "");
        campustext.setText(campus != null ? campus : "");
        departmenttext.setText(department != null ? department : "");
        idtext.setText(id != null ? id : "");
        emailtext.setText(email != null ? email : "");
    }
}
