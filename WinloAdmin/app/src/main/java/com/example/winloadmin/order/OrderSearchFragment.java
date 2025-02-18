package com.example.winloadmin.order;

import static com.example.winloadmin.MainActivity.orderDTOList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.model.OrderRecyclerViewAdapter;
import com.example.winloadmin.model.callback.OrderSearchCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class OrderSearchFragment extends Fragment {

    String orderby;
//    List<OrderDTO> orderDTOListSearchFragment;
    List<OrderDTO> orderDTOListOriginal;
    Spinner spinner;
    public static RecyclerView recyclerViewOrderSearchFragment;

    public OrderSearchFragment() {
//        this.orderDTOListSearchFragment = orderDTOList;
//        this.orderDTOListOriginal = orderDTOList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderDTOListOriginal = orderDTOList;
        orderby = view.getContext().getString(R.string.oldest);

        recyclerViewOrderSearchFragment = view.findViewById(R.id.orderRecyclerView);
        // load orders
//        loadOrderList();

        // sort by options
        loadSpinner(view);
        sortOrders(view);

        // search by id
        TextInputEditText searchTextView = view.findViewById(R.id.searchText);
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                spinner.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

                String searchText = searchTextView.getText().toString();
                if(searchText.isEmpty()){

                    orderDTOList.clear();
                    orderDTOList.addAll(orderDTOListOriginal);

                    spinner.setEnabled(true);
                    sortOrders(view);

                }

            }
        });

        Button searchBtn = view.findViewById(R.id.button2);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = searchTextView.getText().toString();
                if(!searchText.isEmpty()){

                    searchOrder(searchText, new OrderSearchCallback() {
                        @Override
                        public void onOrderSearch(boolean isSuccess, QuerySnapshot documentSnapshots) {
                            if(isSuccess) {

                                orderDTOList.clear();
                                List<OrderDTO> ol = new ArrayList<>();

                                List<DocumentSnapshot> dssl = documentSnapshots.getDocuments();
                                for (DocumentSnapshot documentSnapshot : dssl) {

                                    OrderDTO orderDTO = documentSnapshot.toObject(OrderDTO.class);
                                    if (orderDTO.getOrder_id().contains(searchText)) {

                                        ol.add(0, orderDTO);
                                    }

                                }

                                Log.i("wdwdwd",new Gson().toJson(orderDTOList));
                                Log.i("wdwdwd",new Gson().toJson(orderDTOListOriginal));
//                                recyclerViewOrderSearchFragment.getAdapter().notifyDataSetChanged();
                                RecyclerView recyclerView = view.findViewById(R.id.orderRecyclerView);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                OrderRecyclerViewAdapter adapter = new OrderRecyclerViewAdapter(getParentFragmentManager(),ol);
                                recyclerView.setAdapter(adapter);
//                                recyclerView.setAdapter();
                                orderDTOList.clear();
                                orderDTOList.addAll(orderDTOListOriginal);
                            }
                        }
                    });

                }

            }
        });

    }

    private void searchOrder(String searchText, OrderSearchCallback orderSearchCallback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("order")
                .orderBy("order_id")  // Ensure this field is indexed in Firestore
                .startAt(searchText)  // Matches terms starting with searchText
                .endAt(searchText + "\uf8ff")  // Unicode character to match variations
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        orderSearchCallback.onOrderSearch(true,queryDocumentSnapshots);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        orderSearchCallback.onOrderSearch(false,null);
                    }
                });

//        for(OrderDTO orderDTO : orderDTOList){
//
//            if(orderDTO.getOrder_id().contains(searchText)) {
//
////                orderDTOList.clear();
//                orderDTOList.add(0,orderDTO);
//                recyclerViewOrderSearchFragment.getAdapter().notifyDataSetChanged();
//            }
//
//        }

    }

    private void loadSpinner(View view) {

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.oldest));
        list.add(getString(R.string.newest));
        list.add(getString(R.string.cash));
        list.add(getString(R.string.online));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                list
        );

        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                orderby = parent.getItemAtPosition(position).toString();
                sortOrders(view);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                orderby = getString(R.string.oldest);
                sortOrders(view);

            }
        });


    }

    private void sortOrders(View view) {

        if (orderby.equals(getString(R.string.oldest))) {
            orderDTOList.sort((o1, o2) -> Long.compare(o1.getDate_time(), o2.getDate_time()));
        }

        if (orderby.equals(getString(R.string.newest))) {
            orderDTOList.sort((o1, o2) -> Long.compare(o2.getDate_time(), o1.getDate_time()));
        }

        if (orderby.equals(getString(R.string.cash))) {
            orderDTOList.sort((o1, o2) -> o1.getPayment_method().compareTo(o2.getPayment_method()));
        }

        if (orderby.equals(getString(R.string.online))) {
            orderDTOList.sort((o1, o2) -> o2.getPayment_method().compareTo(o1.getPayment_method()));
        }

        RecyclerView recyclerView = view.findViewById(R.id.orderRecyclerView);
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }

    }

    private void loadOrderList() {

//        recyclerViewOrderSearchFragment = getView().findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewOrderSearchFragment.setLayoutManager(linearLayoutManager);

        OrderRecyclerViewAdapter adapter = new OrderRecyclerViewAdapter(getParentFragmentManager(), orderDTOList);
        recyclerViewOrderSearchFragment.setAdapter(adapter);

    }
}