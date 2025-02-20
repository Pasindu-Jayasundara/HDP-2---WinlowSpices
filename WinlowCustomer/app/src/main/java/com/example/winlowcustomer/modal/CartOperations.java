package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.MainActivity.sqliteVersion;
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
import com.example.winlowcustomer.modal.callback.GetFirebaseDocumentSnapshot;
import com.example.winlowcustomer.modal.callback.LoginCallback;
import com.example.winlowcustomer.modal.callback.ProductAddToCartCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartOperations {

    static SharedPreferences sharedPreferences;

    public static void isLoggedIn(Context context, LoginCallback loginCallback) {

        sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user", null);

        if (user == null) {

            Log.i("CartOperations", "isLoggedIn: 2 abc 1");

            SQLiteHelper sqLiteHelper = new SQLiteHelper(context, "winlow.db", null, sqliteVersion);
            sqLiteHelper.getUser(sqLiteHelper, new GetDataCallback() {
                @Override
                public void onGetData(Cursor cursor) {
                    if (cursor.getCount() == 1) {
                        Log.i("CartOperations", "isLoggedIn: 2 abc 2");

                        cursor.moveToFirst();

                        String name = cursor.getString(1);
                        String id = cursor.getString(0);
                        String mobile = cursor.getString(2);
                        String email = cursor.getString(3);

                        List<String> addressList = new ArrayList<>();
                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
                            @Override
                            public void onGetData(Cursor cursor) {
                                Log.i("CartOperations", "isLoggedIn: 2 abc 3");

                                while (cursor.moveToNext()) {
                                    addressList.add(cursor.getString(0));
                                }

                            }
                        });

                        List<String> orderHistoryList = new ArrayList<>();
//                        sqLiteHelper.getAddress(sqLiteHelper, new GetDataCallback() {
//                            @Override
//                            public void onGetData(Cursor cursor) {
//
//                                while (cursor.moveToNext()) {
//                                    orderHistoryList.add(cursor.getString(1));
//                                }
//
//                            }
//                        });

                        List<PaymentCardDTO> paymentCardDTOList = new ArrayList<>();
//                        sqLiteHelper.getPaymentCard(sqLiteHelper, new GetDataCallback() {
//                            @SuppressLint("NewApi")
//                            @Override
//                            public void onGetData(Cursor cursor) {
//
//                                while (cursor.moveToNext()) {
//
//                                    PaymentCardDTO paymentCardDTO = new PaymentCardDTO();
//                                    paymentCardDTO.setCvv(cursor.getString(3));
//                                    paymentCardDTO.setNumber(cursor.getString(2));
//                                    paymentCardDTO.setExpiryDate(LocalDate.parse(cursor.getString(1)));
//
//                                    paymentCardDTOList.add(paymentCardDTO);
//                                }
//
//                            }
//                        });

                        UserDTO userDTO = new UserDTO();
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
                        editor.commit();

                        loginCallback.onLogin(true);
                        Log.i("CartOperations", "isLoggedIn: 2"+ gson.toJson(userDTO));

                    }
                }
            });

        } else {
            loginCallback.onLogin(true);
        }

    }

    public void addToCart(ProductDTO productDTO, Activity activity, ProductAddToCartCallback productAddToCartCallback) {

//        boolean loggedIn =
        CartOperations.isLoggedIn(activity.getApplicationContext(), new LoginCallback() {
            @Override
            public void onLogin(boolean isSuccess) {

                if(isSuccess){

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

//                                    cart[
//                                          {
//                                              ref_path:"dwednwjndwdnwoid",
//                                              weight_category:[
//                                                                  {
//                                                                      weight: 50,
//                                                                      qty:2
//                                                                  }
//                                                              ],
//                                          }
//                                        ]


                                        // updated new cart list

                                        // get cart data
                                        List<Map<String, Object>> cartData = (List<Map<String, Object>>) documentSnapshot.get("cart");
                                        List<Map<String, Object>> sendCartData = new ArrayList<>();

//                                Log.i("cart", "onSuccess: " + gson.toJson(weightHashMap));

                                        if (cartData != null) {
                                            boolean isProductUpdated = false;

                                            // Loop through existing cart items
                                            for (Map<String, Object> cartItem : cartData) {
                                                String referencePath = (String) cartItem.get("ref_path");
                                                Log.i("cart", "onSuccess: 2 " + gson.toJson(weightHashMap));

                                                if (referencePath.equals(productDTO.getReferencePath())) {
                                                    // Same product, update the weight categories


                                                    // Get the existing weight categories
                                                    List<Map<String,Object>> cartWeightCategoryDTOList = (List<Map<String,Object>>) cartItem.get("weight_category");

                                                    for(Map<String,Object> cartWeightCategoryDTO : cartWeightCategoryDTOList){

                                                        double weight = Double.parseDouble(String.valueOf(cartWeightCategoryDTO.get("weight")));
                                                        int qty1 = Integer.parseInt(String.valueOf(cartWeightCategoryDTO.get("qty")));

                                                        if (weightHashMap.containsKey(weight)) {
                                                            int qty = weightHashMap.get(weight);

                                                            // Update the weight category
                                                            CartWeightCategoryDTO cartWeightCategoryDTO1 = new CartWeightCategoryDTO();
                                                            cartWeightCategoryDTO1.setWeight(weight);
                                                            cartWeightCategoryDTO1.setQty(qty);

                                                            List<CartWeightCategoryDTO> ncd = new ArrayList<>();
                                                            ncd.add(cartWeightCategoryDTO1);

                                                            Map<String, Object> newCartEntry = new HashMap<>();
                                                            newCartEntry.put("ref_path", productDTO.getReferencePath());
//                                                    newCartEntry.put("name", productDTO.getName());
                                                            newCartEntry.put("weight_category", ncd);

                                                            sendCartData.add(newCartEntry);
                                                            weightHashMap.remove(weight); // Remove from map after updating
                                                        }else{

                                                            CartWeightCategoryDTO cartWeightCategoryDTO1 = new CartWeightCategoryDTO();
                                                            cartWeightCategoryDTO1.setWeight(weight);
                                                            cartWeightCategoryDTO1.setQty(qty1);

                                                            List<CartWeightCategoryDTO> ncd = new ArrayList<>();
                                                            ncd.add(cartWeightCategoryDTO1);

                                                            Map<String, Object> newCartEntry = new HashMap<>();
                                                            newCartEntry.put("ref_path", productDTO.getReferencePath());
//                                                    newCartEntry.put("name", productDTO.getName());
                                                            newCartEntry.put("weight_category", ncd);

                                                            sendCartData.add(newCartEntry);
                                                        }

                                                    }

                                                    isProductUpdated = true;
                                                    break;
                                                } else{
                                                    sendCartData.add(cartItem);
                                                }
                                            }

                                            // If no product matched, add new cart entry
                                            if (!isProductUpdated) {

                                                List<CartWeightCategoryDTO> newCartWeightCategoryDTOList = new ArrayList<>();

//                                        Set<Double> doubles = weightHashMap.keySet();
//                                        for (double weight : doubles) {
//                                            CartWeightCategoryDTO cartWeightCategoryDTO = new CartWeightCategoryDTO();
//                                            cartWeightCategoryDTO.setWeight(weight);
//                                            cartWeightCategoryDTO.setQty(weightHashMap.get(weight));
//
//                                            newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);
//                                        }

                                                for (Map.Entry<Double, Integer> entry : weightHashMap.entrySet()) {
                                                    CartWeightCategoryDTO cartWeightCategoryDTO = new CartWeightCategoryDTO();
                                                    cartWeightCategoryDTO.setWeight(entry.getKey());

                                                    Integer qty = entry.getValue();
                                                    cartWeightCategoryDTO.setQty(qty != null ? qty : 0); // Avoid NPE

                                                    newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);
                                                }

                                                Map<String, Object> newCartEntry = new HashMap<>();
                                                newCartEntry.put("ref_path", productDTO.getReferencePath());
//                                        newCartEntry.put("name", productDTO.getName());
                                                newCartEntry.put("weight_category", newCartWeightCategoryDTOList);

                                                cartData.add(newCartEntry);

                                                // Update the Firestore
                                                firestore.collection("user")
                                                        .document(userDTO.getId())
                                                        .update("cart", cartData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                productAddToCartCallback.onAddingToCart(true,R.string.add_to_cart_success);

//                                                                Toast.makeText(activity, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                productAddToCartCallback.onAddingToCart(false,R.string.add_to_cart_failed);

//                                                                Toast.makeText(activity, R.string.add_to_cart_failed, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }else{

                                                // Update the Firestore
                                                firestore.collection("user")
                                                        .document(userDTO.getId())
                                                        .update("cart", sendCartData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                productAddToCartCallback.onAddingToCart(true,R.string.add_to_cart_success);

//                                                                Toast.makeText(activity, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                productAddToCartCallback.onAddingToCart(false,R.string.add_to_cart_failed);

//                                                                Toast.makeText(activity, R.string.add_to_cart_failed, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }

                                        } else {
                                            // If no cart exists, create a new entry

                                            List<CartWeightCategoryDTO> newCartWeightCategoryDTOList = new ArrayList<>();

                                            if (!weightHashMap.isEmpty()) {

                                                Set<Double> doubles = weightHashMap.keySet();

                                                for (double weight : doubles) {
                                                    CartWeightCategoryDTO cartWeightCategoryDTO = new CartWeightCategoryDTO();
                                                    cartWeightCategoryDTO.setWeight(weight);
                                                    cartWeightCategoryDTO.setQty(weightHashMap.get(weight));

                                                    newCartWeightCategoryDTOList.add(cartWeightCategoryDTO);
                                                }
                                            }

                                            // Create the new cart data

                                            Map<String, Object> newCartEntry = new HashMap<>();
                                            newCartEntry.put("ref_path", productDTO.getReferencePath());
//                                    newCartEntry.put("name", productDTO.getName());
                                            newCartEntry.put("weight_category", newCartWeightCategoryDTOList);

                                            List<Map<String, Object>> newCartData = new ArrayList<>();
                                            newCartData.add(newCartEntry);

                                            // Update the Firestore document with the new cart data
                                            firestore.collection("user")
                                                    .document(userDTO.getId())
                                                    .set(Collections.singletonMap("cart", newCartData),SetOptions.merge())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            productAddToCartCallback.onAddingToCart(true,R.string.add_to_cart_success);

//                                                            Toast.makeText(activity, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            productAddToCartCallback.onAddingToCart(false,R.string.add_to_cart_failed);

//                                                            Toast.makeText(activity, R.string.add_to_cart_failed, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        productAddToCartCallback.onAddingToCart(false,R.string.no_user);

                                        Log.i("cart", "onFailure: " + e.getMessage());
                                    }
                                });

                    }

                }else{

                    productAddToCartCallback.onAddingToCart(false,R.string.not_logged_in);

//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Snackbar.make(activity.findViewById(R.id.coordinatorLayout), R.string.not_logged_in, Snackbar.LENGTH_LONG)
//                                    .setAction(R.string.not_logged_in_btn, new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Gson gson = new Gson();
//
//                                            Intent intent = new Intent(activity, LoginActivity.class);
//                                            intent.putExtra("fromCart", gson.toJson(true));
//                                            intent.putExtra("productDTO", gson.toJson(productDTO));
//                                            activity.startActivity(intent);
//
//                                        }
//                                    })
//                                    .show();
//
//                        }
//                    });

                }

            }
        });

    }

    public void loadCart(GetFirebaseDocumentSnapshot getFirebaseDocumentSnapshot, Activity activity, UserDTO userDTO){

        CartOperations.isLoggedIn(activity.getApplicationContext(), new LoginCallback() {
            @Override
            public void onLogin(boolean isSuccess) {

                if(isSuccess){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("user").document(userDTO.getId()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    getFirebaseDocumentSnapshot.onGetDocumentSnapshot(documentSnapshot);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    getFirebaseDocumentSnapshot.onGetDocumentSnapshot(null);
                                }
                            });
                }else{
                    Snackbar.make(activity.findViewById(R.id.coordinatorLayout), R.string.not_logged_in, Snackbar.LENGTH_LONG)
                            .setAction(R.string.not_logged_in_btn, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(intent);

                                }
                            })
                            .show();
                }

            }
        });

    }
}
