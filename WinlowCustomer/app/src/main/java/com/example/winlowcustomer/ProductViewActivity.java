package com.example.winlowcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.CartOperations;
import com.example.winlowcustomer.modal.SingleProductViewRecyclerViewAdapter;
import com.example.winlowcustomer.modal.Translate;
import com.example.winlowcustomer.modal.callback.TranslationCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.winlowcustomer.MainActivity.language;
import static com.example.winlowcustomer.modal.SetUpLanguage.setAppLanguage;

public class ProductViewActivity extends AppCompatActivity {

    ProductDTO productDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        setAppLanguage(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        Intent intent = getIntent();
        String productDTOString = intent.getStringExtra("productDTO");

        Gson gson = new Gson();
        productDTO = gson.fromJson(productDTOString, ProductDTO.class);

        // load product data
        TextView productTitle = findViewById(R.id.textView5);
        TextView productName = findViewById(R.id.textView6);
        TextView productCategory = findViewById(R.id.textView7);
        TextView productDiscount = findViewById(R.id.textView59);


        // image load
        ImageView imageView = findViewById(R.id.imageView2);
        if (productDTO != null && productDTO.getImagePath() != null) {
            Glide.with(this)
                    .load(productDTO.getImagePath())
                    .placeholder(R.drawable.product_placeholder)
                    .error(R.drawable.product_placeholder)
                    .into(imageView);
        } else {
            Log.i("productDTO", "productDTO: " + productDTO);
            Log.i("productDTO", "product im ph: " + productDTO.getImagePath());

        }

        Translate.translateText(productDTO.getName(), language, new TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        productTitle.setText(translatedText);
                        productName.setText(translatedText);
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        productTitle.setText(productDTO.getName());
                        productName.setText(productDTO.getName());

                    }
                });

            }
        });

        Translate.translateText(productDTO.getCategory(), language, new TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        productCategory.setText(translatedText);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        productCategory.setText(productDTO.getCategory());
                    }
                });

            }
        });

        if (productDTO.getDiscount() > 0) {
            productDiscount.setVisibility(View.VISIBLE);
            String discountTxt = String.valueOf(productDTO.getDiscount()) + "% Off";
            productDiscount.setText(discountTxt);
        } else {
            productDiscount.setVisibility(View.GONE);
        }

        // load weight categories
        RecyclerView recyclerView = findViewById(R.id.weightRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product")
                .document(productDTO.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {

                        DocumentSnapshot document = task.getResult();

                        // Get the array field
                        List<Map<String, Object>> weightCategories = (List<Map<String, Object>>) document.get("weight_category");

                        if (weightCategories != null) {

                            List<WeightCategoryDTO> weightCategoryDTOList = new ArrayList<>();

                            for (Map<String, Object> weightCategory : weightCategories) {

                                double weight;
                                double unitPrice;

//                                try {

                                    weight = Double.parseDouble(String.valueOf(weightCategory.get("weight")));
                                    unitPrice = Double.parseDouble(String.valueOf(weightCategory.get("unit_price")));

//                                }catch (Exception e){
//
//                                    weight = (long) weightCategory.get("weight");
//                                    unitPrice = (long) weightCategory.get("unit_price");
//
//                                }


                                WeightCategoryDTO weightCategoryDTO = new WeightCategoryDTO();
                                weightCategoryDTO.setWeight(weight);
                                weightCategoryDTO.setUnitPrice(unitPrice);

                                weightCategoryDTOList.add(weightCategoryDTO);

                            }
                            productDTO.setWeightCategoryDTOList(weightCategoryDTOList);

                        }

                        if(productDTO.getWeightCategoryDTOList() != null && !productDTO.getWeightCategoryDTOList().isEmpty()){
                            SingleProductViewRecyclerViewAdapter singleProductViewRecyclerViewAdapter = new SingleProductViewRecyclerViewAdapter(productDTO);
                            recyclerView.setAdapter(singleProductViewRecyclerViewAdapter);
                        }

                    }

                });


        // back button
        ImageButton backButton = findViewById(R.id.imageButton);
        ImageButton backButton2 = findViewById(R.id.imageButton3);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        backButton2.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // add to cart
        Button addToCart = findViewById(R.id.button3);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SingleProductViewRecyclerViewAdapter.weightHashMap != null && !SingleProductViewRecyclerViewAdapter.weightHashMap.isEmpty()) {
                    CartOperations cartOperations = new CartOperations();
                    cartOperations.addToCart(productDTO, ProductViewActivity.this);
                }else{
                    Toast.makeText(ProductViewActivity.this,R.string.add_to_cart_qty,Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SingleProductViewRecyclerViewAdapter.weightHashMap = null;
    }
}