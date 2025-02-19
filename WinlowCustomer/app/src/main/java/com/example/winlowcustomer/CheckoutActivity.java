package com.example.winlowcustomer;

import static com.example.winlowcustomer.MainActivity.language;
import static com.example.winlowcustomer.modal.AddressHandling.loadAddress;
import static com.example.winlowcustomer.modal.CartRecyclerViewAdapter.checkoutProductList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.CartWeightCategoryDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.CartOperations;
import com.example.winlowcustomer.modal.CartRecyclerViewAdapter;
import com.example.winlowcustomer.modal.Payhere;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.callback.ConvertToFirebaseCallback;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;
import com.example.winlowcustomer.modal.callback.LoginCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    UserDTO userDTO;
    HashMap<String, Object> paymentData = new HashMap<>();
    String totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView9, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        if (userJson == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        Gson gson = new Gson();
        userDTO = gson.fromJson(userJson, UserDTO.class);

        if (getIntent().hasExtra("cameFrom")) {

            String from = getIntent().getStringExtra("cameFrom");
            if (from != null && from.equals("addNewAddress")) {

                String paymentData1 = getIntent().getStringExtra("paymentData");
                paymentData = gson.fromJson(paymentData1, HashMap.class);

                totalPrice = getIntent().getStringExtra("totalPrice");

            }
        } else {
            totalPrice = getIntent().getStringExtra("total_price");
        }

        //get total price
        TextView totalPriceView = findViewById(R.id.textView18);
        totalPriceView.setText(totalPrice);

        // back
        ImageButton back = findViewById(R.id.imageButton20);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // load Address
        Spinner spinner = findViewById(R.id.spinner2);
        Button addAddressBtn = findViewById(R.id.button19);
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CheckoutActivity.this, AddressActivity.class);
                intent.putExtra("from", "checkout");
                intent.putExtra("paymentData", gson.toJson(paymentData));
                intent.putExtra("userDto", gson.toJson(userDTO));
                intent.putExtra("totalPrice", totalPrice);
                startActivity(intent);

            }
        });


        loadAddress(getApplicationContext(), new GetAddressCallback() {
            @Override
            public void onAddressLoaded(List<String> addressList) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (addressList.contains(getString(R.string.checkout_select_address))) {
                            List<String> list = new ArrayList<>();
                            list.add(getString(R.string.checkout_select_address));
                            list.add(getString(R.string.select_address));
                            addressList.removeAll(list);
                        }
                        if (addressList.isEmpty()) {
                            addAddressBtn.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.GONE);
                        } else {
                            addAddressBtn.setVisibility(View.GONE);
                            spinner.setVisibility(View.VISIBLE);


                            // load spinner
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getApplicationContext(),
                                    R.layout.checkout_selected_address_layout,
                                    addressList
                            );
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(arrayAdapter);
                        }

                    }
                });

            }
        });

        // name
        TextInputEditText deliveryNameView = findViewById(R.id.checkoutName);
        deliveryNameView.setText(userDTO.getName());

        // mobile
        TextInputEditText deliveryMobileView = findViewById(R.id.checkoutMobile);
        deliveryMobileView.setText(userDTO.getMobile());

        // proceed to payment
        Button proceedToPaymentButton = findViewById(R.id.button12);
        proceedToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deliveryNameView.getText().toString().isBlank()) {
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (deliveryMobileView.getText().toString().isBlank()) {
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_mobile, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(spinner.getVisibility() == View.VISIBLE){
                    if (spinner.getSelectedItem().toString().equals(getString(R.string.select_address)) ||
                            spinner.getSelectedItem().toString().equals(getString(R.string.checkout_select_address))) {
                        Toast.makeText(CheckoutActivity.this, R.string.checkout_need_address, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(CheckoutActivity.this, R.string.add_adderss, Toast.LENGTH_SHORT).show();
                    return;
                }


                List<CartDTO> checkoutProductList = CartRecyclerViewAdapter.checkoutProductList;
                if (checkoutProductList == null) {
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_product, Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioGroup radioGroup = findViewById(R.id.radioGroup);
                boolean payOnline = false;
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton6) { // online
                    payOnline = true;
                }

                Log.i("purchase", "1");

                purchase(payOnline);


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAddress(getApplicationContext(), new GetAddressCallback() {
            @Override
            public void onAddressLoaded(List<String> addressList) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Button addAddressBtn = findViewById(R.id.button19);
                        Spinner spinner = findViewById(R.id.spinner2);

                        if (addressList.contains(getString(R.string.checkout_select_address))) {
                            List<String> list = new ArrayList<>();
                            list.add(getString(R.string.checkout_select_address));
                            list.add(getString(R.string.select_address));
                            addressList.removeAll(list);
                        }
                        if (addressList.isEmpty()) {
                            addAddressBtn.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.GONE);
                        } else {
                            addAddressBtn.setVisibility(View.GONE);
                            spinner.setVisibility(View.VISIBLE);

                            // load spinner
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getApplicationContext(),
                                    R.layout.checkout_selected_address_layout,
                                    addressList
                            );
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    spinner.setAdapter(arrayAdapter);
                                }
                            });
                        }

                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 22620 && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {

            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            Log.d("paymentpayhere", "Result:" + response.toString());

            if (resultCode == Activity.RESULT_OK) {

                String msg;

                if (response != null) {

                    if (response.isSuccess()) {
                        msg = "Success:" + response.getData().toString();
                        Log.d("paymentpayhere", msg);

                        Toast.makeText(CheckoutActivity.this, R.string.payment_success, Toast.LENGTH_SHORT).show();
                        convertToFirebase("Online", new ConvertToFirebaseCallback() {
                            @Override
                            public void onConvert(boolean isSuccess, Map<String, Object> map) {
                                if (isSuccess) {
                                    addToFirebase(map);
                                }
                            }
                        });

                    } else {
                        msg = "Failed:" + response.getData().toString();
                        Log.d("paymentpayhere", msg);

                        Toast.makeText(CheckoutActivity.this, R.string.payment_failed, Toast.LENGTH_SHORT).show();

                    }

                } else {
                    msg = "Result: no response";
                    Log.d("paymentpayhere", msg);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(CheckoutActivity.this, R.string.payment_cancelled, Toast.LENGTH_SHORT).show();

                if (response != null) {
                    Log.d("paymentpayhere", "canceled: null" + response.toString());
                } else {
                    Log.d("paymentpayhere", "User canceled the request");
                }

            }
        }
    }

    private void convertToFirebase(String paymentMethod, ConvertToFirebaseCallback convertToFirebaseCallback) {

        Map<String, Object> map = new HashMap<>();

        CartOperations.isLoggedIn(getApplicationContext(), new LoginCallback() {
            @Override
            public void onLogin(boolean isSuccess) {

                if (isSuccess) {

                    SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
                    String userTxt = sharedPreferences.getString("user", null);
                    if (userTxt != null) {

                        Gson gson = new Gson();
                        UserDTO userDTO = gson.fromJson(userTxt, UserDTO.class);

                        map.put("user_id", userDTO.getId());
                        map.put("order_list", paymentData.get("items"));
                        map.put("payment_method", paymentMethod);
                        map.put("date_time", System.currentTimeMillis());
                        map.put("order_status", "Pending");
                        map.put("order_id", paymentData.get("orderId"));

                        convertToFirebaseCallback.onConvert(true, map);

                    }

                } else {
                    convertToFirebaseCallback.onConvert(false, null);

                    Toast.makeText(getApplicationContext(), R.string.not_logged_in, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void addToFirebase(Map<String, Object> map) {
        Log.i("purchase", "7");

        String orderId = (String) paymentData.get("orderId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("order")
                .document(orderId)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("purchase", "8");


                        db.collection("user").document(userDTO.getId()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Map<String, Object> ohMap = new HashMap<>();

                                        List<String> orderHistoryList = (List<String>) documentSnapshot.get("order_history");
                                        if (orderHistoryList == null) {
                                            orderHistoryList = new ArrayList<>();
                                        }

                                        orderHistoryList.add(orderId);
                                        ohMap.put("order_history", orderHistoryList);
                                        Log.i("purchase", "10");

                                        db.collection("user")
                                                .document(userDTO.getId())
                                                .set(ohMap, SetOptions.mergeFields("order_history"))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.i("purchase", "11");

                                                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_success, Toast.LENGTH_SHORT).show();

                                                        Gson gson = new Gson();

                                                        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                                                        intent.putExtra("order_id", orderId);
                                                        intent.putExtra("itemList", gson.toJson(paymentData.get("items")));
                                                        intent.putExtra("total", totalPrice);

                                                        startActivity(intent);
                                                        finish();


                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_failed, Toast.LENGTH_SHORT).show();

//                                        Gson gson = new Gson();

                                                        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_failed, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
//                        List<String> orderHistory = userDTO.getOrderHistory();
//                        orderHistory.add(orderId);
//                        userDTO.setOrderHistory(orderHistory);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_failed, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private void purchase(boolean payOnline) {
        Log.i("purchase", "2");

        // price
//        totalPrice = getIntent().getStringExtra("total_price");
        String total = totalPrice.replaceAll("[^0-9.]", "");

        // address
        Spinner spinner = findViewById(R.id.spinner2);
        String selectedAddress = spinner.getSelectedItem().toString();

        // name
        TextInputEditText deliveryNameView = findViewById(R.id.checkoutName);
        String deliveryName = deliveryNameView.getText().toString();
        String[] s = deliveryName.split(" ");

        String firstName = s[0];
        String lastName = "";
        if (s.length == 2) {
            lastName = s[1];
        }

        // mobile
        TextInputEditText deliveryMobileView = findViewById(R.id.checkoutMobile);
        String deliveryMobile = deliveryMobileView.getText().toString();

        // email
        String email = userDTO.getEmail();

        // items
        List<Item> items = new ArrayList<>();

        for (CartDTO cartDTO : checkoutProductList) {
            Log.i("purchase", "3");

            double totalPrice2 = 0.0;

            List<CartWeightCategoryDTO> cartWeightCategoryDTOList = cartDTO.getCartWeightCategoryDTOList();
            for (CartWeightCategoryDTO cartWeightCategoryDTO : cartWeightCategoryDTOList) {

                double weight = cartWeightCategoryDTO.getWeight();
                double qty = cartWeightCategoryDTO.getQty();

                ProductDTO product = cartDTO.getProduct();

                List<WeightCategoryDTO> weightCategoryDTOList = product.getWeightCategoryDTOList();
                for (WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList) {
                    if (weightCategoryDTO.getWeight() == weight) {
                        totalPrice2 += weightCategoryDTO.getUnitPrice() * qty;
                        break;
                    }
                }
                String name = product.getName() + "  (" + String.valueOf(cartDTO.getWeight()) + ")";
                items.add(new Item(product.getId(), name, (int) qty, totalPrice2));

            }

        }

        Log.i("purchase", "4");

        // order id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String orderId = db.collection("orders").document().getId();

        paymentData.put("id", userDTO.getId());
        paymentData.put("firstName", firstName);
        paymentData.put("lastName", lastName);
        paymentData.put("email", email);
        paymentData.put("mobile", deliveryMobile);
        paymentData.put("address", selectedAddress);
        paymentData.put("totalPrice", total);
        paymentData.put("orderId", orderId);
        paymentData.put("items", items);
        paymentData.put("activity", CheckoutActivity.this);
        paymentData.put("requestId", 22620);

//        checkoutProductList.clear();

        if (payOnline) {
            Log.i("purchase", "5");

            Payhere.pay(paymentData, CheckoutActivity.this);
        } else {
            Log.i("purchase", "6");

            convertToFirebase("Cash On Delivery", new ConvertToFirebaseCallback() {
                @Override
                public void onConvert(boolean isSuccess, Map<String, Object> map) {

                    if (isSuccess) {
                        addToFirebase(map);
                    }

                }
            });

//            Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
//            startActivity(intent);
//            finish();

        }

    }

}