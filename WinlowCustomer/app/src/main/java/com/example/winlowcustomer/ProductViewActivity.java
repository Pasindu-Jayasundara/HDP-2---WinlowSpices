package com.example.winlowcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.winlowcustomer.dto.ProductDTO;

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

        Intent intent = new Intent();
        productDTO = (ProductDTO) intent.getSerializableExtra("productDTO");

        // load product data
        TextView productTitle = findViewById(R.id.textView5);
        TextView productName = findViewById(R.id.textView6);
        TextView productCategory = findViewById(R.id.textView7);
        TextView productDiscount = findViewById(R.id.textView59);

        // image load
        ImageView imageView = findViewById(R.id.imageView2);
        Glide.with(this) // Use context
                .load(productDTO.getImagePath()) // Load image URL
                .placeholder(R.drawable.product_placeholder) // Optional: placeholder image
                .error(R.drawable.product_placeholder) // Optional: error image
                .into(imageView); // Set image into ImageView

        productTitle.setText(productDTO.getName());
        productName.setText(productDTO.getName());
        productCategory.setText(productDTO.getCategory());
        if (productDTO.getDiscount() > 0) {
            productDiscount.setVisibility(View.VISIBLE);
            String discountTxt = String.valueOf(productDTO.getDiscount()) + "% Off";
            productDiscount.setText(discountTxt);
        } else {
            productDiscount.setVisibility(View.GONE);
        }

        // back button
        ImageButton backButton = findViewById(R.id.imageButton);
        ImageButton backButton2 = findViewById(R.id.imageButton3);
        backButton.setOnClickListener(v -> finish());
        backButton2.setOnClickListener(v -> finish());

        // add to cart
        Button addToCart = findViewById(R.id.button3);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }
}