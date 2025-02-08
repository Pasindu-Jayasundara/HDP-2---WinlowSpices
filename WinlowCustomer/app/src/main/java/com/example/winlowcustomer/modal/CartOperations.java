package com.example.winlowcustomer.modal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.winlowcustomer.LoginActivity;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.ProductDTO;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class CartOperations {

    private static boolean isLoggedIn(){

        return false;
    }
    public void addToCart(ProductDTO productDTO, Activity activity){

        if(CartOperations.isLoggedIn()){




        }else{
            Snackbar.make(activity.findViewById(R.id.coordinatorLayout), R.string.not_logged_in, Snackbar.LENGTH_LONG)
                    .setAction(R.string.not_logged_in_btn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Gson gson = new Gson();

                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.putExtra("fromCart", gson.toJson(true));
                            intent.putExtra("productDTO", gson.toJson(productDTO));
                            activity.startActivity(intent);

                        }
                    })
                    .show();
        }

    }
}
