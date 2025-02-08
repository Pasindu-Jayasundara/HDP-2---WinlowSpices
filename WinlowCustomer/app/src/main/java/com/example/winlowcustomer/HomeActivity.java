package com.example.winlowcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.modal.NetworkConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // bottom navigation
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        // data load
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);

        String category = sharedPreferences.getString("category", null);
        String product = sharedPreferences.getString("product", null);
        String banner = sharedPreferences.getString("banner", null);

        if (category != null) {
            HashSet<String> categoryHashSet = gson.fromJson(category, HashSet.class);
            loadCategories(categoryHashSet);
        }

        if(product != null){
            HashMap<String, ProductDTO> productHashMap = gson.fromJson(product, HashMap.class);
            loadProducts(productHashMap);
        }

        if(banner != null){
            ArrayList<BannerDTO> bannerArrayList = gson.fromJson(banner, ArrayList.class);
            loadBanners(bannerArrayList);
        }


        // check network connection
        NetworkConnection.register(getApplicationContext());

        // click search
        SearchBar searchbar = findViewById(R.id.searchBar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(SearchProductActivity.class);

            }
        });

        // click profile image
        ImageView profileImage = findViewById(R.id.imageView);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(AccountActivity.class);

            }
        });

        // click location
        TextView selectLocation = findViewById(R.id.textView4);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeActivity(SelectLocationActivity.class);
            }
        });

        // change category
        ChipGroup categoryChipGroup = findViewById(R.id.categoryChipGroup);
        categoryChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                changeProductOrder(checkedIds);

            }
        });

    }

    private void loadBanners(ArrayList<BannerDTO> bannerArrayList) {

        for(BannerDTO bannerDTO:bannerArrayList){

        }

    }

    private void loadProducts(HashMap<String, ProductDTO> productHashMap) {

        productHashMap.forEach((string, productDTO) -> {

        });

    }

    private void loadCategories(HashSet<String> categoryHashSet) {

        boolean isFirstTime = true;
        ChipGroup chipGroup = findViewById(R.id.categoryChipGroup);

        for(String category : categoryHashSet){

            Chip chip = new Chip(new ContextThemeWrapper(this, com.mobven.progress.R.style.Widget_MaterialComponents_Chip_Filter));
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);

            if (isFirstTime){
                chip.setChecked(true);
                isFirstTime = false;
                chipGroup.removeAllViews();
            } else{
                chip.setChecked(false);
            }

            chipGroup.addView(chip);
        }

    }

    private void changeProductOrder(List<Integer> checkedIds) {


    }

    private void changeActivity(Class<?> activityName) {

        Intent intent = new Intent(HomeActivity.this, activityName);
        startActivity(intent);

    }

}