package com.example.winlowcustomer;

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
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.CartRecyclerViewAdapter;
import com.example.winlowcustomer.modal.Payhere;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    UserDTO userDTO;
    HashMap<String,Object> paymentData = new HashMap<>();

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

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data",MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        if(userJson == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        Gson gson = new Gson();
        userDTO = gson.fromJson(userJson, UserDTO.class);


        //get total price
        String totalPrice = getIntent().getStringExtra("total_price");
        TextView totalPriceView = findViewById(R.id.textView18);
        totalPriceView.setText(totalPrice);

        // load Address
        Spinner spinner = findViewById(R.id.spinner2);
        Button addAddressBtn = findViewById(R.id.button19);

        List<String> addressList = loadAddress(getApplicationContext());
        if(addressList.size() == 1){
            addAddressBtn.setVisibility(View.VISIBLE);
        }else{
            addAddressBtn.setVisibility(View.GONE);

            // load spinner
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    R.layout.selected_address_layout,
                    addressList
            );
            arrayAdapter.setDropDownViewResource(R.layout.address_dropdown_layout);
            spinner.setAdapter(arrayAdapter);
        }

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

                if(deliveryNameView.getText().toString().isBlank()){
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(deliveryMobileView.getText().toString().isBlank()){
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_mobile, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(spinner.getSelectedItemId() == 0){
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_address, Toast.LENGTH_SHORT).show();
                    return;
                }

                List<CartDTO> checkoutProductList = CartRecyclerViewAdapter.checkoutProductList;
                if(checkoutProductList == null){
                    Toast.makeText(CheckoutActivity.this, R.string.checkout_need_product, Toast.LENGTH_SHORT).show();
                    return;
                }

                purchase();


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

                if (response != null){

                    if (response.isSuccess()){
                        msg = "Success:" + response.getData().toString();
                        Log.d("paymentpayhere", msg);

                        Toast.makeText(CheckoutActivity.this, R.string.payment_success, Toast.LENGTH_SHORT).show();
                        addToFirebase(checkoutProductList);

                    }else{
                        msg = "Failed:" + response.getData().toString();
                        Log.d("paymentpayhere", msg);

                        Toast.makeText(CheckoutActivity.this, R.string.payment_failed, Toast.LENGTH_SHORT).show();

                    }

                } else{
                    msg = "Result: no response";
                    Log.d("paymentpayhere", msg);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(CheckoutActivity.this, R.string.payment_cancelled, Toast.LENGTH_SHORT).show();

                if (response != null) {
                    Log.d("paymentpayhere","canceled: null"+ response.toString());
                } else {
                    Log.d("paymentpayhere", "User canceled the request");
                }

            }
        }
    }

    private void addToFirebase(List<CartDTO> checkoutProductList){

        String orderId = (String) paymentData.get("orderId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("order")
                .document(orderId)
                .set(checkoutProductList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                        List<String> orderHistory = userDTO.getOrderHistory();
                        orderHistory.add(orderId);
                        userDTO.setOrderHistory(orderHistory);

                        db.collection("user")
                                .document(userDTO.getId())
                                .update("order_history",userDTO.getOrderHistory())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_success, Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_failed, Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckoutActivity.this, R.string.order_placed_failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void purchase(){

        // price
        String totalPrice = getIntent().getStringExtra("total_price");
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
        if(s.length == 2){
            lastName = s[1];
        }

        // mobile
        TextInputEditText deliveryMobileView = findViewById(R.id.checkoutMobile);
        String deliveryMobile = deliveryMobileView.getText().toString();

        // email
        String email = userDTO.getEmail();

        // items
        List<Item> items = new ArrayList<>();
        for(CartDTO cartDTO : checkoutProductList){

            double totalWeightPrice = 0.0;

            List<WeightCategoryDTO> weightCategoryDTOList = cartDTO.getProduct().getWeightCategoryDTOList();
            for(WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList){

                double weight = weightCategoryDTO.getWeight();
                int qty = cartDTO.getCartWeightCategoryDTOList().get(0).getQty();

                totalWeightPrice += weightCategoryDTO.getUnitPrice() * qty;

                items.add(new Item(cartDTO.getProduct().getId(), cartDTO.getProduct().getName(), qty, totalWeightPrice));

            }

        }

//        items.add(new Item("1", "Door  ", 1, 1000.0));
//        items.add(new Item("2", "Door bell ", 1, 1000.0));
//        items.add(new Item("3", "Door bell wireless", 1, 1000.0));

        // order id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String orderId = db.collection("orders").document().getId();

        paymentData.put("id",userDTO.getId());
        paymentData.put("firstName",firstName);
        paymentData.put("lastName",lastName);
        paymentData.put("email",email);
        paymentData.put("mobile",deliveryMobile);
        paymentData.put("address",selectedAddress);
        paymentData.put("totalPrice",total);
        paymentData.put("orderId",orderId);
        paymentData.put("items",items);
        paymentData.put("activity",CheckoutActivity.this);
        paymentData.put("requestId",22620);

        Payhere.pay(paymentData);

    }

}