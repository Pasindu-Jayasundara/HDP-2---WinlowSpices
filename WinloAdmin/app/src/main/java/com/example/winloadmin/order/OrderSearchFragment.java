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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class OrderSearchFragment extends Fragment {

    String orderby = getString(R.string.oldest);
//    List<OrderDTO> orderDTOListSearchFragment;
    List<OrderDTO> orderDTOListOriginal;
    Spinner spinner;
    public static RecyclerView recyclerViewOrderSearchFragment;

    public OrderSearchFragment() {
//        this.orderDTOListSearchFragment = orderDTOList;
        this.orderDTOListOriginal = orderDTOList;
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

        // sort by options
        loadSpinner(view);
        sortOrders();

        // load orders
        loadOrderList();

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
                    sortOrders();

                }

            }
        });

        Button searchBtn = view.findViewById(R.id.button2);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = searchTextView.getText().toString();
                if(!searchText.isEmpty()){

                    searchOrder(searchText);

                }

            }
        });

    }

    private void searchOrder(String searchText) {

        for(OrderDTO orderDTO : orderDTOList){

            if(orderDTO.getOrder_id().contains(searchText)) {

//                orderDTOList.clear();
                orderDTOList.add(0,orderDTO);
                recyclerViewOrderSearchFragment.getAdapter().notifyDataSetChanged();
            }

        }

    }

    private void loadSpinner(View view) {

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.oldest));
        list.add(getString(R.string.newest));
        list.add(getString(R.string.cash));
        list.add(getString(R.string.online));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                list
        );

        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                orderby = parent.getItemAtPosition(position).toString();
                sortOrders();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                orderby = getString(R.string.oldest);
                sortOrders();

            }
        });


    }

    private void sortOrders() {

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

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    private void loadOrderList() {

        recyclerViewOrderSearchFragment = getView().findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewOrderSearchFragment.setLayoutManager(linearLayoutManager);

        OrderRecyclerViewAdapter adapter = new OrderRecyclerViewAdapter(getParentFragmentManager());
        recyclerViewOrderSearchFragment.setAdapter(adapter);

    }
}