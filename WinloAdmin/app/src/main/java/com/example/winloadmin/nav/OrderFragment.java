package com.example.winloadmin.nav;

import static com.example.winloadmin.MainActivity.orderCount;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.order.AllOrderFragment;
import com.example.winloadmin.order.OrderSearchFragment;
import com.example.winloadmin.product.ProductSearchFragment;

import java.util.List;

public class OrderFragment extends Fragment {

    List<OrderDTO> orderDTOList;
    public OrderFragment(List<OrderDTO> orderDTOList) {
        this.orderDTOList = orderDTOList;
    }

    public OrderFragment(){
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
        FragmentContainerView fragmentContainerView = view.findViewById(R.id.fragmentContainerView);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new OrderSearchFragment())
                .setReorderingAllowed(true)
                .commit();
        fragmentContainerView.setVisibility(View.GONE);

        CardView searchOrderCardView = view.findViewById(R.id.searchOrderCardView);
        searchOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FragmentManager fragmentManager = getParentFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragmentContainerView, new OrderSearchFragment())
//                        .setReorderingAllowed(true)
//                        .commit();
                if(fragmentContainerView.getVisibility() == View.GONE){
                    fragmentContainerView.setVisibility(View.VISIBLE);
                    fragmentContainerView.setAlpha(0f);
                    fragmentContainerView.animate().alpha(1f).setDuration(1000).start();
                }else{
                    fragmentContainerView.animate().alpha(0f).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            fragmentContainerView.setVisibility(View.GONE);
                        }
                    }).start();
                }

            }
        });

        // all orders
        FragmentContainerView fragmentContainerView2 = view.findViewById(R.id.fragmentContainerView20);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView20, new AllOrderFragment())
                .setReorderingAllowed(true)
                .commit();
        fragmentContainerView2.setVisibility(View.GONE);

        CardView allOrderCardView = view.findViewById(R.id.addOrderCardView);
        allOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FragmentManager fragmentManager = getParentFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragmentContainerView20, new AllOrderFragment())
//                        .setReorderingAllowed(true)
//                        .commit();

                if(fragmentContainerView2.getVisibility() == View.GONE){
                    fragmentContainerView2.setVisibility(View.VISIBLE);
                    fragmentContainerView2.setAlpha(0f);
                    fragmentContainerView2.animate().alpha(1f).setDuration(1000).start();
                }else{
                    fragmentContainerView2.animate().alpha(0f).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            fragmentContainerView2.setVisibility(View.GONE);
                        }
                    }).start();
                }

            }
        });

    }
}