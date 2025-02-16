package com.example.winloadmin.nav;

import static com.example.winloadmin.MainActivity.orderCount;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.order.AllOrderFragment;
import com.example.winloadmin.order.OrderSearchFragment;

import java.util.List;

public class OrderFragment extends Fragment {

    List<OrderDTO> orderDTOList;
    public OrderFragment(List<OrderDTO> orderDTOList) {
        this.orderDTOList = orderDTOList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // order count
        TextView orderCountView = view.findViewById(R.id.textView12);
        orderCountView.setText(String.valueOf(orderCount));

        // search orders
        CardView searchOrderCardView = view.findViewById(R.id.searchOrderCardView);
        searchOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, new OrderSearchFragment())
                        .setReorderingAllowed(true)
                        .commit();

            }
        });

        // all orders
        CardView allOrderCardView = view.findViewById(R.id.addOrderCardView);
        allOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView20, new AllOrderFragment())
                        .setReorderingAllowed(true)
                        .commit();

            }
        });

    }
}