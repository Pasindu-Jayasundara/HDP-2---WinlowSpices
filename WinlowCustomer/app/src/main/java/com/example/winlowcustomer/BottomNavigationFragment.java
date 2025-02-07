package com.example.winlowcustomer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BottomNavigationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // home img
        ImageView imageView = getView().findViewById(R.id.imageView7);
        if(imageView!=null){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity(HomeActivity.class);
                }
            });
        }

        // home txt
        TextView textView = getView().findViewById(R.id.textView55);
        if(textView!=null){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity(HomeActivity.class);
                }
            });
        }

        // order img
        ImageView orderImg = getView().findViewById(R.id.imageView10);
        if(orderImg!=null){
            orderImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity(OrderHistoryActivity.class);
                }
            });
        }

        // order txt
        TextView orderTxt = getView().findViewById(R.id.textView56);
        if(orderTxt!=null){
            orderTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity(OrderHistoryActivity.class);
                }
            });
        }

        // cart img
        ImageView cartImg = getView().findViewById(R.id.imageView11);
        if(cartImg!=null){
            cartImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity(CartActivity.class);
                }
            });
        }

        // cart txt
        TextView cartTxt = getView().findViewById(R.id.textView57);
        if(cartTxt!=null){
            cartTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoActivity(CartActivity.class);
                }
            });
        }
    }

    private void gotoActivity(Class<?> destinationActivity) {

        Intent intent = new Intent(getActivity(), destinationActivity);
        startActivity(intent);

    }
}