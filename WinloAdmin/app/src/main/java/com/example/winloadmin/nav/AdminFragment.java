package com.example.winloadmin.nav;

import static com.example.winloadmin.LoginActivity.adminHashMap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.winloadmin.R;
import com.example.winloadmin.admin.AddNewAdminFragment;
import com.example.winloadmin.dto.AdminDTO;
import com.example.winloadmin.model.AdminRecyclerViewAdapter;
import com.example.winloadmin.product.ProductSearchFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminFragment extends Fragment {

    public static List<AdminDTO> adminDTOList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadAdmins(view);

        CardView cardView = view.findViewById(R.id.addNewAdminCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminFragmentContainerView, new AddNewAdminFragment())
                        .setReorderingAllowed(true)
                        .commit();

            }
        });
    }

    private void loadAdmins(View view) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin")
                .where(Filter.notEqualTo("email",adminHashMap.get("email").toString()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot documentSnapshot:documentSnapshotList){

                            AdminDTO object = documentSnapshot.toObject(AdminDTO.class);
                            adminDTOList.add(object);

                        }

                        loadAdminRecyclerView(view);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),R.string.no_admin,Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadAdminRecyclerView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdminRecyclerViewAdapter adminRecyclerViewAdapter = new AdminRecyclerViewAdapter(adminDTOList);
        recyclerView.setAdapter(adminRecyclerViewAdapter);

    }
}