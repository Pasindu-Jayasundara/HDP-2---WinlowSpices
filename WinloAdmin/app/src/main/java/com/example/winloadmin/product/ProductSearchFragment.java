package com.example.winloadmin.product;

import static com.example.winloadmin.MainActivity.orderDTOList;
import static com.example.winloadmin.MainActivity.productDTOList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.ProductDTO;
import com.example.winloadmin.model.ProductRecyclerViewAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchFragment extends Fragment {

    String searchBy;
    List<ProductDTO> originalProductDTOList;
    public static RecyclerView recyclerView;
//    public static ProductRecyclerViewAdapter productRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        originalProductDTOList = productDTOList;

        // search txt
        TextInputEditText searchTxt = view.findViewById(R.id.searchProductText);
        searchTxt.setHint(R.string.hint_product_name);
        searchBy = "name";

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(searchTxt.getText().toString().isEmpty()){

                    ProductRecyclerViewAdapter productRecyclerViewAdapter = new ProductRecyclerViewAdapter(new ArrayList<>());
                    recyclerView.setAdapter(productRecyclerViewAdapter);
//                    productDTOList.clear();
//                    productDTOList.addAll(originalProductDTOList);
//
//                    recyclerView.getAdapter().notifyDataSetChanged();
//                    productRecyclerViewAdapter.notifyDataSetChanged();



                }
            }
        });

        // Chip group
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                if (checkedIds.contains(R.id.chip3)) { // product name

                    searchTxt.setHint(R.string.hint_product_name);
                    searchBy = "name";

                }
                if (checkedIds.contains(R.id.chip2)) { // product category

                    searchTxt.setHint(R.string.hint_product_category);
                    searchBy = "category";

                }

            }
        });

        // search btn
        Button btn = view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = searchTxt.getText().toString();
                if(!searchText.isBlank()){
                    searchProduct(searchText);
                }else{

                        ProductRecyclerViewAdapter productRecyclerViewAdapter = new ProductRecyclerViewAdapter(new ArrayList<>());
                        recyclerView.setAdapter(productRecyclerViewAdapter);
//                    productDTOList.clear();
//                    productDTOList.addAll(originalProductDTOList);
//
//                    recyclerView.getAdapter().notifyDataSetChanged();
//                    productRecyclerViewAdapter.notifyDataSetChanged();



                }

            }
        });

        // load product
        recyclerView = view.findViewById(R.id.productRecyclerView);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        ProductRecyclerViewAdapter productRecyclerViewAdapter = new ProductRecyclerViewAdapter();
//        recyclerView.setAdapter(productRecyclerViewAdapter);

    }

//    private void searchProduct(String searchText) {
//
//        for(ProductDTO productDTO:productDTOList){
//
//            if(searchBy.equals("name")){
//
//                if(productDTO.getName().toLowerCase().contains(searchText.toLowerCase())){
//                    productDTOList.add(0,productDTO);
//                    productRecyclerViewAdapter.notifyDataSetChanged();
//                }
//
//            }
//
//            if(searchBy.equals("category")){
//
//                if(productDTO.getCategory().toLowerCase().contains(searchText.toLowerCase())){
//                    productDTOList.add(0,productDTO);
//                    productRecyclerViewAdapter.notifyDataSetChanged();
//                }
//            }
//
//        }
//
//    }

    private void searchProduct(String searchText) {
        // Create a temporary list to hold the matching products
        List<ProductDTO> filteredList = new ArrayList<>();

        for (ProductDTO productDTO : originalProductDTOList) {
            if (searchBy.equals("name")) {
                if (productDTO.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(productDTO);
                }
            }

            if (searchBy.equals("category")) {
                if (productDTO.getCategory().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(productDTO);
                }
            }
        }
        Log.d("ProductSearch", "Filtered List: " + filteredList.toString());
        Log.d("ProductSearch", "Updated Product List: " + productDTOList.toString());

        // Now update the main list with the filtered results
//        productDTOList.clear();
//        productDTOList.addAll(filteredList);

        // Notify the adapter that the dataset has changed
//        productRecyclerViewAdapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ProductRecyclerViewAdapter productRecyclerViewAdapter = new ProductRecyclerViewAdapter(filteredList);
        recyclerView.setAdapter(productRecyclerViewAdapter);
//        recyclerView.getAdapter().notifyDataSetChanged();

//        productDTOList.clear();
//        productDTOList.addAll(originalProductDTOList);

    }

}