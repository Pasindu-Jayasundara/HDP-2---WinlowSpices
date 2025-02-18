package com.example.winloadmin.nav;

import static com.example.winloadmin.MainActivity.customerDTOList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.winloadmin.R;
import com.example.winloadmin.model.UserRecyclerViewAdapter;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView totalUserView = view.findViewById(R.id.textView48);
        totalUserView.setText(String.valueOf(customerDTOList.size()));

        RecyclerView recyclerView = view.findViewById(R.id.userRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        UserRecyclerViewAdapter userRecyclerViewAdapter = new UserRecyclerViewAdapter();
        recyclerView.setAdapter(userRecyclerViewAdapter);

    }
}