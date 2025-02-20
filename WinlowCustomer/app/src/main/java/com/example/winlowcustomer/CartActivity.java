package com.example.winlowcustomer;

import static com.example.winlowcustomer.HomeActivity.productDTOArrayListOriginal;
import static com.example.winlowcustomer.MainActivity.language;
import static com.example.winlowcustomer.modal.CartRecyclerViewAdapter.checkoutProductList;
import static com.example.winlowcustomer.modal.SetUpLanguage.setAppLanguage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.CartWeightCategoryDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.CartOperations;
import com.example.winlowcustomer.modal.CartRecyclerViewAdapter;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.callback.GetDataCallback;
import com.example.winlowcustomer.modal.callback.GetFirebaseDocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    TextView totalPriceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        setAppLanguage(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView10, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        // back
        ImageButton back = findViewById(R.id.imageButton2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //get user
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        Gson gson = new Gson();
        UserDTO userDTO = gson.fromJson(userJson, UserDTO.class);

        // hide checkout part
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        Button checkoutButton = findViewById(R.id.button4);

        hideCheckout(tableLayout,checkoutButton);

        // load cart list
        loadCartList(userDTO);

        totalPriceView = findViewById(R.id.textView12);

        // checkout
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkoutProductList.isEmpty()){
                    Toast.makeText(CartActivity.this, R.string.checkout_need_product, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("total_price", totalPriceView.getText().toString());
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //get user
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        Gson gson = new Gson();
        UserDTO userDTO = gson.fromJson(userJson, UserDTO.class);

        // hide checkout part
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        Button btn = findViewById(R.id.button4);
        hideCheckout(tableLayout,btn);

        // load cart list
        loadCartList(userDTO);


    }

    private void loadCartList(UserDTO userDTO) {

        CartOperations cartOperations = new CartOperations();
        cartOperations.loadCart(new GetFirebaseDocumentSnapshot() {
            @Override
            public void onGetDocumentSnapshot(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Map<String, Object> documentSnapshotData = documentSnapshot.getData();

                    if (documentSnapshotData != null && documentSnapshotData.containsKey("cart")) {
                        List<Map<String, Object>> cartDataMapList = (List<Map<String, Object>>) documentSnapshotData.get("cart");

                        if (cartDataMapList != null && !cartDataMapList.isEmpty()) {
                            RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this,LinearLayoutManager.VERTICAL,false));

                            List<CartDTO> cartDTOList = new ArrayList<>();

                            for (int i = 0; i < cartDataMapList.size(); i++) {
                                Map<String, Object> cartDataMap = cartDataMapList.get(i);
                                if (cartDataMap != null && cartDataMap.containsKey("weight_category")) {
                                    List<Map<String, Object>> weightCategoryListMap = (List<Map<String, Object>>) cartDataMap.get("weight_category");

                                    List<CartWeightCategoryDTO> cartWeightCategoryDTOList = new ArrayList<>();
                                    if (weightCategoryListMap != null) {
                                        for (Map<String, Object> weightCategoryMap : weightCategoryListMap) {
                                            if (weightCategoryMap != null) {
                                                CartWeightCategoryDTO cartWeightCategoryDTO = new CartWeightCategoryDTO();

                                                // Safe parsing
                                                Number weightNumber = (Number) weightCategoryMap.get("weight");
                                                Number qtyNumber = (Number) weightCategoryMap.get("qty");

                                                cartWeightCategoryDTO.setWeight(weightNumber != null ? weightNumber.doubleValue() : 0.0);
                                                cartWeightCategoryDTO.setQty(qtyNumber != null ? qtyNumber.intValue() : 0);

                                                cartWeightCategoryDTOList.add(cartWeightCategoryDTO);
                                            }
                                        }
                                    }

                                    CartDTO cartDTO = new CartDTO();
                                    cartDTO.setReferencePath(String.valueOf(cartDataMap.get("ref_path")));

//                                    for(ProductDTO productDTO : productDTOArrayListOriginal){
//                                        if(productDTO.getReferencePath().equals(cartDTO.getReferencePath())){
//                                            cartDTO.setProduct(productDTO);
//                                        }
//                                    }
                                    if (productDTOArrayListOriginal != null) {
                                        for (ProductDTO productDTO : productDTOArrayListOriginal) {
                                            if (cartDTO.getReferencePath() != null && productDTO.getReferencePath() != null) {
                                                if (cartDTO.getReferencePath().equals(productDTO.getReferencePath())) {
                                                    cartDTO.setProduct(productDTO);
                                                }
                                            }
                                        }
                                    } else {
                                        Log.e("CartActivity", "productDTOArrayListOriginal is null");
                                    }


                                    cartDTO.setCartWeightCategoryDTOList(cartWeightCategoryDTOList);

                                    cartDTO.setCartDTOMap(cartDataMap);

                                    cartDTOList.add(cartDTO);
                                }
                            }

                            Gson gson = new Gson();
                            Log.i("cartDTOList",gson.toJson(cartDTOList));
                            // Set adapter

                            TableLayout tableLayout = findViewById(R.id.tableLayout);
                            CartRecyclerViewAdapter cartRecyclerViewAdapter = new CartRecyclerViewAdapter(cartDTOList,userDTO,getApplicationContext(),tableLayout,CartActivity.this);
                            recyclerView.setAdapter(cartRecyclerViewAdapter);

                        } else {
                            Toast.makeText(CartActivity.this, R.string.cart_empty, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CartActivity.this, R.string.cart_empty, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, R.string.cart_empty, Toast.LENGTH_SHORT).show();
                }

            }
        }, CartActivity.this, userDTO);

    }

    public static void hideCheckout(TableLayout tableLayout,Button checkoutBtn) {
        if (tableLayout != null) {
            tableLayout.setVisibility(View.GONE);
            checkoutBtn.setVisibility(View.GONE);
        }
    }

    public static void showChekout(TableLayout tableLayout,Button checkoutBtn) {
        if (tableLayout != null) {
            tableLayout.setVisibility(View.VISIBLE);
            checkoutBtn.setVisibility(View.VISIBLE);
            calculatePriceData(tableLayout);
        }
    }

    public static void calculatePriceData(TableLayout tableLayout){

        double totalPrice = 0.0;

//        Log.i("cpl","checkoutProductList: "+new Gson().toJson(checkoutProductList));
        for(CartDTO cartDTO : checkoutProductList) {

//            double totalWeightPrice = 0.0;

            List<CartWeightCategoryDTO> cartWeightCategoryDTOList = cartDTO.getCartWeightCategoryDTOList();
            for(CartWeightCategoryDTO cartWeightCategoryDTO : cartWeightCategoryDTOList) {

                double weight = cartWeightCategoryDTO.getWeight();
                double qty = cartWeightCategoryDTO.getQty();

                Log.i("cpl","weight: "+weight);
                Log.i("cpl","qty: "+qty);

                ProductDTO product = cartDTO.getProduct();

                List<WeightCategoryDTO> weightCategoryDTOList = product.getWeightCategoryDTOList();
                for (WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList) {
                    if (weightCategoryDTO.getWeight() == weight) {
                        totalPrice += weightCategoryDTO.getUnitPrice() * qty;
                        break;
                    }
                }

//                totalPrice += totalWeightPrice;

            }

        }

        TextView totalPriceView = tableLayout.findViewById(R.id.textView12);

        String totalPriceString = String.format("%.2f", totalPrice);
        totalPriceView.setText("Rs. "+totalPriceString);


    }

}