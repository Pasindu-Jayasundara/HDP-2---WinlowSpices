package com.example.winlowcustomer.modal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.View;
import android.widget.Toast;

import com.example.winlowcustomer.LoginActivity;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.PaymentCardDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.callback.GetDataCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartOperations {

    public static boolean isLoggedIn(Context context) {

        final boolean[] hasLoggedInDataFound = {false};

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data",Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user", null);
        if(user == null){

            SQLiteHelper sqLiteHelper = new SQLiteHelper(context, "winlow.db", null, 1);
            sqLiteHelper.getUser(sqLiteHelper, new GetDataCallback() {
                @Override
                public void onGetData(Cursor cursor) {
                    if(cursor.getCount()==1){
                        hasLoggedInDataFound[0] = true;

                        cursor.moveToFirst();

                        String name = cursor.getString(0);
                        String id = cursor.getString(1);
                        String mobile = cursor.getString(2);
                        String email = cursor.getString(3);

                        List<String> addressList = new ArrayList<>();
                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
                            @Override
                            public void onGetData(Cursor cursor) {

                                while(cursor.moveToNext()){
                                    addressList.add(cursor.getString(0));
                                }

                            }
                        });

                        List<String> orderHistoryList = new ArrayList<>();
                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
                            @Override
                            public void onGetData(Cursor cursor) {

                                while(cursor.moveToNext()){
                                    orderHistoryList.add(cursor.getString(1));
                                }

                            }
                        });

                        List<PaymentCardDTO> paymentCardDTOList = new ArrayList<>();
                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onGetData(Cursor cursor) {

                                while(cursor.moveToNext()){

                                    PaymentCardDTO paymentCardDTO = new PaymentCardDTO();
                                    paymentCardDTO.setCvv(cursor.getString(3));
                                    paymentCardDTO.setNumber(cursor.getString(2));
                                    paymentCardDTO.setExpiryDate(LocalDate.parse(cursor.getString(1)));

                                    paymentCardDTOList.add(paymentCardDTO);
                                }

                            }
                        });

                        UserDTO userDTO = new UserDTO();
                        userDTO.setCart(null);
                        userDTO.setId(id);
                        userDTO.setName(name);
                        userDTO.setAddress(addressList);
                        userDTO.setEmail(email);
                        userDTO.setMobile(mobile);
                        userDTO.setOrderHistory(orderHistoryList);
                        userDTO.setPaymentCard(paymentCardDTOList);

                        Gson gson = new Gson();
                        String json = gson.toJson(userDTO);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user",json);
                        editor.apply();
                    }
                }
            });

        }else{
            hasLoggedInDataFound[0] = true;
            return hasLoggedInDataFound[0];
        }

        return hasLoggedInDataFound[0];
    }

    public void addToCart(ProductDTO productDTO, Activity activity) {

        boolean loggedIn = CartOperations.isLoggedIn(activity.getApplicationContext());

        if (loggedIn) {

            // if not in cart add to cart
            // if in cart add to cart increase qty



        } else {
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
