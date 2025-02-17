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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewAdminFragment extends Fragment {

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

        Button addBtn = view.findViewById(R.id.button12);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!emailView.getText().toString().isBlank()){

                    boolean isFound = false;

                    for(AdminDTO adminDTO:adminDTOList){
                        if(adminDTO.getEmail().equals(emailView.getText().toString())){

                            isFound = true;

                            Toast.makeText(v.getContext(),R.string.already_registered_admin,Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if(!isFound){

                        if(adminHashMap.get("email").toString().equals(emailView.getText().toString())){
                            Toast.makeText(v.getContext(),R.string.already_registered_admin,Toast.LENGTH_SHORT).show();
                        }else{
                            addNewAdmin(v,emailView.getText().toString());
                        }

                    }

                }else{
                    Toast.makeText(v.getContext(),R.string.enter_new_admin_email,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addNewAdmin(View view, String newAdminEmail) {

        Map<String,Object> newAdminMap = new HashMap<>();
        newAdminMap.put("id","Waiting To Login");
        newAdminMap.put("name","Waiting To Login");
        newAdminMap.put("email",newAdminEmail);
        newAdminMap.put("profileImage","");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin")
                .add(newAdminMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        AdminDTO adminDTO = new AdminDTO();
                        adminDTO.setId("Waiting To Login");
                        adminDTO.setName("Waiting To Login");
                        adminDTO.setProfileImage("");
                        adminDTO.setEmail(newAdminEmail);

                        adminDTOList.add(adminDTO);

                        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
                        recyclerView.getAdapter().notifyDataSetChanged();

                        Toast.makeText(view.getContext(),R.string.admin_adding_success,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(),R.string.admin_adding_failed,Toast.LENGTH_SHORT).show();
                    }
                });

    }
}