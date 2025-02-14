package com.example.winlowcustomer;

import static com.example.winlowcustomer.modal.PdfOperations.generateReceiptPDF;
import static com.example.winlowcustomer.modal.PdfOperations.shareReceipt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.PdfOperations;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import lk.payhere.androidsdk.model.Item;

public class OrderSuccessActivity extends AppCompatActivity {

    UserDTO userDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SetUpLanguage.setAppLanguage(getApplicationContext());

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

        Intent receivedIntent = getIntent();
        String orderId = receivedIntent.getStringExtra("order_id");
        String total = receivedIntent.getStringExtra("total");

        String itemListJson = receivedIntent.getStringExtra("itemList");
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> items = gson.fromJson(itemListJson, type);

        String htmlReceipt = PdfOperations.generateReceiptHtml(userDTO.getName(),orderId,total);

        // download receipt
        Button downloadButton = findViewById(R.id.button21);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PdfOperations.printReceipt(htmlReceipt,OrderSuccessActivity.this);

            }
        });

        // email receipt
        Button emailButton = findViewById(R.id.button20);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PdfOperations.emailReceipt(getApplicationContext(),htmlReceipt,getString(R.string.receipt),new String[]{userDTO.getEmail()},OrderSuccessActivity.this);
            }
        });

        // share receipt
        TextView shareButton = findViewById(R.id.textView81);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File receiptFile = generateReceiptPDF(getApplicationContext(), userDTO.getName(), orderId, total);

                // Call the method to share the generated PDF file
                shareReceipt(getApplicationContext(), receiptFile, OrderSuccessActivity.this);


            }
        });

        // go to cart
        Button done = findViewById(R.id.button22);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderSuccessActivity.this, CartActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }
}