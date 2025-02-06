package com.example.winlowcustomer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.modal.NetworkConnection;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.search.SearchBar;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // check network connection
        NetworkConnection.register(getApplicationContext());

        // click search
        SearchBar searchbar = findViewById(R.id.searchBar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(SearchProductActivity);

            }
        });

        // click profile image
        ImageView profileImage = findViewById(R.id.imageView);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(ProfileActivity);

            }
        });

        // click location
        TextView selectLocation = findViewById(R.id.textView4);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(SelectLocationActivity);
            }
        });

        // change category
        ChipGroup categoryChipGroup = findViewById(R.id.categoryChipGroup);
        categoryChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                if(checkedIds.contains("chipAll")){
                    loadProduct("a");
                }

            }
        });

    }

    private void changeActivity(){
        Intent intent = new Intent(HomeActivity.this, activity);
        startActivity(intent);
    }

}