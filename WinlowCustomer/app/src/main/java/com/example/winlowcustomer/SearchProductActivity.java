package com.example.winlowcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.modal.HomeRecyclerViewAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class SearchProductActivity extends AppCompatActivity {

    ArrayList<ProductDTO> productDTOArrayList;
    ArrayList<ProductDTO> productDTOArrayListOriginal;
    HashSet<String> categoryHashSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // bottom navigation
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView2, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();


        // load data
        loadData();

        //search
        TextInputEditText searchTxt = findViewById(R.id.searchText);
        searchTxt.requestFocus();
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    filterProducts(searchTxt.getText().toString().trim());
                    return true;
                }

                return false;
            }
        });
//        searchTxt.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    filterProducts(searchTxt.getText().toString().trim());
//                    return true;
//                }
//
//                return false;
//            }
//        });
        ImageButton searchBtn = findViewById(R.id.imageButton4);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterProducts(searchTxt.getText().toString().trim());

            }
        });

    }

    private void filterProducts(String searchTxt) {

        Log.i("searchTxt", searchTxt);

        ChipGroup categoryChipGroup = findViewById(R.id.chipGroup2);
        int checkedChipId = categoryChipGroup.getCheckedChipId();
        Chip chip = findViewById(checkedChipId);

        String chipTxt = chip.getText().toString();


        List<ProductDTO> reorderedList = new ArrayList<>();
        Iterator<ProductDTO> iterator = productDTOArrayList.iterator();

        Log.i("searchTxt", "11111111111");

        boolean isCleanNew = false;

        while (iterator.hasNext()) {
            Log.i("searchTxt", "222222222222");
            ProductDTO productDTO = iterator.next();

            if(chipTxt.equals("All") && !searchTxt.isEmpty()){

                Log.i("searchTxt", "3333333333333");

                if (productDTO.getName().toLowerCase().contains(searchTxt.toLowerCase())) {
                    Log.i("searchTxt", "444444444");

                    reorderedList.add(productDTO);
                    iterator.remove(); // Safe removal using iterator
                }
            }else if(chipTxt.equals("All")){// no txt
//                productDTOArrayList =
                isCleanNew = true;
                reorderedList = new ArrayList<>(productDTOArrayListOriginal);

            }else{
                if (productDTO.getCategory().equals(chipTxt) && productDTO.getName().toLowerCase().contains(searchTxt.toLowerCase())) {
                    reorderedList.add(productDTO);
                    iterator.remove(); // Safe removal using iterator
                }
            }
        }

        if(!isCleanNew){
            productDTOArrayList.addAll(0, reorderedList); // Add at the beginning
        }else{
            productDTOArrayList.clear();
            productDTOArrayList.addAll(reorderedList);
        }

//        if(chipTxt.equals("All")){
//            Log.i("searchTxt", String.valueOf(productDTOArrayList));
//            Log.i("searchTxt", "222222222222");
//
//        }else{
//
            Log.i("searchTxt", String.valueOf(productDTOArrayList));
//
//        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }


    private void loadData(){

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);

        String category = sharedPreferences.getString("category", null);
        String product = sharedPreferences.getString("product", null);

        if (category != null) {
            Type listType = new TypeToken<HashSet<String>>() {}.getType();
            categoryHashSet = gson.fromJson(category, listType);

            ChipGroup chipGroup = findViewById(R.id.chipGroup2);

            new HomeActivity().loadCategories(categoryHashSet,chipGroup,this);
        }

        if(product != null){
            Type listType = new TypeToken<ArrayList<ProductDTO>>() {}.getType();
            productDTOArrayList = gson.fromJson(product, listType);
            productDTOArrayListOriginal = gson.fromJson(product, listType);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        HomeRecyclerViewAdapter homeRecyclerViewAdapter = new HomeRecyclerViewAdapter(productDTOArrayList);
        recyclerView.setAdapter(homeRecyclerViewAdapter);

    }
}