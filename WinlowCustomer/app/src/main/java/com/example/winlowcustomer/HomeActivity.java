package com.example.winlowcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.modal.MainLoadData;
import com.example.winlowcustomer.modal.NetworkConnection;
import com.example.winlowcustomer.modal.HomeRecyclerViewAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.search.SearchBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselGravity;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ArrayList<ProductDTO> productDTOArrayList;
    ArrayList<BannerDTO> bannerArrayList;
    HashSet<String> categoryHashSet;

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
        loadData();




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

        // refresh
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                MainLoadData.mainLoadData(productDTOArrayList, bannerArrayList, categoryHashSet, false, HomeActivity.this);
                loadData();

                RecyclerView recyclerView = findViewById(R.id.recyclerView2);
                recyclerView.getAdapter().notifyDataSetChanged();


                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void loadData(){

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);

        String category = sharedPreferences.getString("category", null);
        String product = sharedPreferences.getString("product", null);
        String banner = sharedPreferences.getString("banner", null);

        if (category != null) {
            Type listType = new TypeToken<HashSet<String>>() {}.getType();
            categoryHashSet = gson.fromJson(category, listType);

            ChipGroup chipGroup = findViewById(R.id.categoryChipGroup);

            loadCategories(categoryHashSet,chipGroup);
        }

        if(product != null){
            Type listType = new TypeToken<ArrayList<ProductDTO>>() {}.getType();
            productDTOArrayList = gson.fromJson(product, listType);
        }

        if(banner != null){

            Type listType = new TypeToken<ArrayList<BannerDTO>>() {}.getType();
            bannerArrayList = gson.fromJson(banner, listType);
            loadBanner();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        HomeRecyclerViewAdapter homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(productDTOArrayList);
        recyclerView.setAdapter(homeRecyclerViewAdapter);

    }

    public void loadCategories(HashSet<String> categoryHashSet,ChipGroup chipGroup) {

        boolean isFirstTime = true;

        for(String category : categoryHashSet){

            Chip chip = new Chip(new ContextThemeWrapper(this, com.mobven.progress.R.style.Widget_MaterialComponents_Chip_Filter));
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);

            chip.setId(View.generateViewId());

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

        Chip chip = findViewById(checkedIds.get(0));
        String chipTxt = chip.getText().toString();

        for(ProductDTO productDTO : productDTOArrayList){
            if(productDTO.getCategory().equals(chipTxt)){
                productDTOArrayList.remove(productDTO);
                productDTOArrayList.add(0, productDTO);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    private void loadBanner(){

        ImageCarousel imageCarousel = findViewById(R.id.imageCarousel);

        if(bannerArrayList.isEmpty()){
            imageCarousel.setVisibility(View.GONE);
            return;
        }
        List<CarouselItem> carouselItemList = new ArrayList<>();

        BannerDTO dto = bannerArrayList.get(0);
        for(String imagePath : dto.getImagePathList()){

            CarouselItem carouselItem = new CarouselItem(imagePath);
            carouselItemList.add(carouselItem);
        }

        imageCarousel.setAutoPlay(true);
        imageCarousel.setCarouselType(CarouselType.SHOWCASE);
        imageCarousel.setData(carouselItemList);

    }

    private void changeActivity(Class<?> activityName) {

        Intent intent = new Intent(HomeActivity.this, activityName);
        startActivity(intent);

    }

}