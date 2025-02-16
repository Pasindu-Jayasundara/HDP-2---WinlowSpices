package com.example.winloadmin.order;

import static com.example.winloadmin.MainActivity.orderDTOList;
import static com.example.winloadmin.order.OrderSearchFragment.recyclerViewOrderSearchFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.winloadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusUpdateFragment extends Fragment {

    String orderStatus;
    String orderId;

    public OrderStatusUpdateFragment(String orderStatus, String orderId) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_status_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // spinner
        Spinner spinner = view.findViewById(R.id.spinner3);

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.pending));
        list.add(getString(R.string.ready_to_deliver));
        list.add(getString(R.string.delivering));
        list.add(getString(R.string.delivered));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                list
        );

        int index = list.indexOf(orderStatus);

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(index);

        // update btn
        Button btn = view.findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDatabase();

            }
        });

    }

    private void updateDatabase() {

        Spinner spinner = getView().findViewById(R.id.spinner3);
        String newStatus = spinner.getSelectedItem().toString();

        if(newStatus.equals(orderStatus)){
            return;
        }

        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        db.collection("order")
                .document(orderId)
                .update("order_status",orderStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), R.string.order_status_updated, Toast.LENGTH_SHORT).show();

                        orderStatus = newStatus;
                        orderDTOList.forEach(orderDTO -> {
                            if(orderDTO.getOrder_id().equals(orderId)){
                                orderDTO.setOrder_status(newStatus);
                            }
                        });
                        recyclerViewOrderSearchFragment.getAdapter().notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.order_status_update_failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}