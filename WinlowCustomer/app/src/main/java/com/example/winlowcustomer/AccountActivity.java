package com.example.winlowcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // profile edit card view
        CardView profileEditCardView = findViewById(R.id.cardView2);
        profileEditCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(ProfileActivity.class);
            }
        });

        // profile edit img
        Button profileEditImg = findViewById(R.id.imageButton6);
        profileEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(ProfileActivity.class);
            }
        });

        // profile edit arrow
        Button profileEditArrow = findViewById(R.id.imageButton7);
        profileEditArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(ProfileActivity.class);
            }
        });

        // profile address card view
        CardView profileAddressCardView = findViewById(R.id.cardView3);
        profileAddressCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(AddressActivity.class);
            }
        });

        // profile address img
        Button profileAddressImg = findViewById(R.id.imageButton8);
        profileAddressImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(AddressActivity.class);
            }
        });

        // profile address arrow
        Button profileAddressArrow = findViewById(R.id.imageButton9);
        profileAddressCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(AddressActivity.class);
            }
        });

        // profile language card view
        CardView profileLanguageCardView = findViewById(R.id.cardView5);
        profileLanguageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LanguageActivity.class);
            }
        });

        // profile language img
        Button profileLanguageImg = findViewById(R.id.imageButton10);
        profileLanguageImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LanguageActivity.class);
            }
        });

        // profile language arrow
        Button profileLanguageArrow = findViewById(R.id.imageButton11);
        profileLanguageArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LanguageActivity.class);
            }
        });

        // profile payment card view
        CardView profilePaymentCardView = findViewById(R.id.cardView6);
        profilePaymentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoActivity(PaymentCardActivity.class);
            }
        });

        // profile payment img
        Button profilePaymentImg = findViewById(R.id.button5);
        profilePaymentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoActivity(PaymentCardActivity.class);
            }
        });

        // profile payment arrow
        Button profilePaymentArrow = findViewById(R.id.button7);
        profilePaymentArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gotoActivity(PaymentCardActivity.class);
            }
        });

    }

    private void gotoActivity(Class<?> activity) {

        Intent intent = new Intent(AccountActivity.this,activity);
        startActivity(intent);
    }
}