package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.modal.SingleProductViewRecyclerViewAdapter.weightHashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.winlowcustomer.LoginActivity;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.CartWeightCategoryDTO;
import com.example.winlowcustomer.dto.PaymentCardDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.callback.GetDataCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartOperations {

    static SharedPreferences sharedPreferences;

    public static boolean isLoggedIn(Context context) {

        final boolean[] hasLoggedInDataFound = {false};

        sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user", null);
        if (user == null) {

            SQLiteHelper sqLiteHelper = new SQLiteHelper(context, "winlow.db", null, 1);
            sqLiteHelper.getUser(sqLiteHelper, new GetDataCallback() {
                @Override
                public void onGetData(Cursor cursor) {
                    if (cursor.getCount() == 1) {
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

                                while (cursor.moveToNext()) {
                                    addressList.add(cursor.getString(0));
                                }

                            }
                        });

                        List<String> orderHistoryList = new ArrayList<>();
                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
                            @Override
                            public void onGetData(Cursor cursor) {

                                while (cursor.moveToNext()) {
                                    orderHistoryList.add(cursor.getString(1));
                                }

                            }
                        });

                        List<PaymentCardDTO> paymentCardDTOList = new ArrayList<>();
                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onGetData(Cursor cursor) {

                                while (cursor.moveToNext()) {

                                    PaymentCardDTO paymentCardDTO = new PaymentCardDTO();
                                    paymentCardDTO.setCvv(cursor.getString(3));
                                    paymentCardDTO.setNumber(cursor.getString(2));
                                    paymentCardDTO.setExpiryDate(LocalDate.parse(cursor.getString(1)));

                                    paymentCardDTOList.add(paymentCardDTO);
                                }

                            }
                        });

                        UserDTO userDTO = new UserDTO();
//                        userDTO.setCart(null);
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
                        editor.putString("user", json);
                        editor.apply();
                    }
                }
            });

        } else {
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

            String userTxt = sharedPreferences.getString("user", null);
            if (userTxt != null) {

                Gson gson = new Gson();
                UserDTO userDTO = gson.fromJson(userTxt, UserDTO.class);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("user").document(userDTO.getId()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                // get cart data
                                List<CartDTO> cartDTOList = new ArrayList<>();
                                List<Map<String, Object>> cartData = (List<Map<String, Object>>) documentSnapshot.get("cart");


                                // updated new cart list
                                List<CartWeightCategoryDTO> newCartWeightCategoryDTOList = new ArrayList<>();

                                if (cartData != null) {

                                    for (Map<String, Object> cartItem : cartData) {

                                        String referencePath = (String) cartItem.get("ref_path");

                                        if (referencePath.equals(productDTO.getReferencePath())) {
                                            // same product

                                            // get weight category list
                                            List<CartWeightCategoryDTO> cartWeightCategoryDTOList = (List<CartWeightCategoryDTO>) cartItem.get("weight_category");
                                            for (CartWeightCategoryDTO cartWeightCategoryDTO : cartWeightCategoryDTOList) {

                                                if (weightHashMap.containsKey(cartWeightCategoryDTO.getWeight())) {
                                                    // same weight as user selected

                                                    int qty = weightHashMap.get(cartWeightCategoryDTO.getWeight());
                                                    cartWeightCategoryDTO.setQty(qty);

                                                    // after getting qty value remove weight and qty from map
                                                    weightHashMap.remove(cartWeightCategoryDTO.getWeight());

                                                    newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);
                                                } else {
                                                    newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);
                                                }

                                            }

                                            // still has weight in map
                                            if (!weightHashMap.isEmpty()) {
                                                // add new weight category

                                                for (Long weight : weightHashMap.keySet()) {

                                                    CartWeightCategoryDTO cartWeightCategoryDTO = new CartWeightCategoryDTO();
                                                    cartWeightCategoryDTO.setWeight(weight);
                                                    cartWeightCategoryDTO.setQty(weightHashMap.get(weight));

                                                    newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);

                                                    weightHashMap.remove(weight);
                                                }

                                            }

                                            break;
                                        }
                                    }

                                    // add updated cart list to firebase

//                                    Map<String, Object> updates = new HashMap<>();
//                                    updates.put("cart", newCartWeightCategoryDTOList);

                                    firestore.collection("user")
                                            .document(userDTO.getId())
                                            .update("cart", newCartWeightCategoryDTOList)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Toast.makeText(activity, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(activity, R.string.add_to_cart_failed, Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                } else {
                                    // add new cart list to firebase

                                    if (!weightHashMap.isEmpty()) {
                                        // add new weight category

                                        for (Long weight : weightHashMap.keySet()) {

                                            CartWeightCategoryDTO cartWeightCategoryDTO = new CartWeightCategoryDTO();
                                            cartWeightCategoryDTO.setWeight(weight);
                                            cartWeightCategoryDTO.setQty(weightHashMap.get(weight));

                                            newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);

                                            weightHashMap.remove(weight);
                                        }

                                    }
//
//                                    Map<String, Object> cart = new HashMap<>();
//                                    cart.put("ref_path", productDTO.getReferencePath());
//                                    cart.put("weight_category", newCartWeightCategoryDTOList);
//
                                    firestore.collection("user")
                                            .document(userDTO.getId())
                                            .update("cart", newCartWeightCategoryDTOList)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Toast.makeText(activity, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(activity, R.string.add_to_cart_failed, Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.i("cart", "onFailure: " + e.getMessage());
                            }
                        });

            }

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
