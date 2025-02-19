package com.example.winloadmin.admin;

import static com.example.winloadmin.LoginActivity.adminHashMap;
import static com.example.winloadmin.nav.AdminFragment.adminDTOList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.AdminDTO;
import com.example.winloadmin.model.AdminRecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewAdminFragment extends Fragment {

    RecyclerView recyclerView;

    public AddNewAdminFragment(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText emailView = view.findViewById(R.id.newAdminEmailAddress);
        TextInputEditText nameView = view.findViewById(R.id.newAdminName);

        Button addBtn = view.findViewById(R.id.button12);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!nameView.getText().toString().isBlank()) {

                    if (!emailView.getText().toString().isBlank()) {

                        boolean isFound = false;

                        for (AdminDTO adminDTO : adminDTOList) {
                            if (adminDTO.getEmail().equals(emailView.getText().toString())) {

                                isFound = true;

                                Toast.makeText(v.getContext(), R.string.already_registered_admin, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if (!isFound) {

                            if (adminHashMap.get("email").toString().equals(emailView.getText().toString())) {
                                Toast.makeText(v.getContext(), R.string.already_registered_admin, Toast.LENGTH_SHORT).show();
                            } else {
                                addNewAdmin(view, emailView.getText().toString(),nameView.getText().toString());
                            }

                        }

                    } else {
                        Toast.makeText(v.getContext(), R.string.enter_new_admin_email, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(v.getContext(), R.string.enter_new_admin_name, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addNewAdmin(View view, String newAdminEmail, String name) {

        Map<String, Object> newAdminMap = new HashMap<>();
        newAdminMap.put("name", name);
        newAdminMap.put("email", newAdminEmail);
        newAdminMap.put("profileImage", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin")
                .document(newAdminEmail) // ✅ Setting the email as document ID
                .set(newAdminMap) // ✅ Use 'set()' instead of 'add()'
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        AdminDTO adminDTO = new AdminDTO();
                        adminDTO.setName(name);
                        adminDTO.setProfileImage("");
                        adminDTO.setEmail(newAdminEmail);

                        adminDTOList.add(adminDTO);

//                        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
                        if(recyclerView.getAdapter()==null){
                            recyclerView.setAdapter(new AdminRecyclerViewAdapter(adminDTOList, recyclerView));
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }else{
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                        TextInputEditText emailView = view.findViewById(R.id.newAdminEmailAddress);
                        TextInputEditText nameView = view.findViewById(R.id.newAdminName);
                        emailView.setText("");
                        nameView.setText("");

                        Toast.makeText(view.getContext(), R.string.admin_adding_success, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), R.string.admin_adding_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}