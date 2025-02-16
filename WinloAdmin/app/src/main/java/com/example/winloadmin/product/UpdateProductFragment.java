package com.example.winloadmin.product;

import static com.example.winloadmin.MainActivity.productDTOList;
import static com.example.winloadmin.product.ProductSearchFragment.productRecyclerViewAdapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.ProductDTO;
import com.example.winloadmin.dto.WeightCategoryDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProductFragment extends Fragment {

    ProductDTO productDTO;
    Map<String,Object> updateMap = new HashMap<>();
    RecyclerView weightCategoryRecyclerView;
    List<WeightCategoryDTO> weightCategoryList;
    boolean isWeightListChanged;

    public UpdateProductFragment(ProductDTO productDTO, RecyclerView weightCategoryRecyclerView) {
        this.productDTO = productDTO;
        this.weightCategoryRecyclerView = weightCategoryRecyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadProductData(view);

        // new weight
        EditText newWeightView = view.findViewById(R.id.editTextText);
        EditText newPriceView = view.findViewById(R.id.editTextText2);
        Button addBtn = view.findViewById(R.id.button8);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWeightList(newWeightView.getText().toString(),newPriceView.getText().toString(),v);
            }
        });

        // save updates
        Button saveBtn = view.findViewById(R.id.button9);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isChanged = isChanged();
                if(isChanged){
                    updateProduct(v.getContext());
                }else{
                    Toast.makeText(v.getContext(),R.string.no_changes,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isChanged() {

        boolean isChanged = false;

        String name = productDTO.getName();
        String category = productDTO.getCategory();
        String availability = productDTO.getStock();

        String newName = productDTO.getName();
        String newCategory = productDTO.getCategory();
        String newAvailability = productDTO.getStock();

        if(!name.equals(newName) || !category.equals(newCategory) || !availability.equals(newAvailability) || isWeightListChanged){

            updateMap.put("name",newName);
            updateMap.put("category",newCategory);
            updateMap.put("stock",newAvailability);
            updateMap.put("weight_category",weightCategoryList);

            isChanged = true;
        }

        return isChanged;
    }

    private void addToWeightList(String newWeight, String newPrice, View v) {

        WeightCategoryDTO weightCategoryDTO = new WeightCategoryDTO();
        weightCategoryDTO.setWeight(Integer.parseInt(newWeight));
        weightCategoryDTO.setUnit_price(Integer.parseInt(newPrice));

        weightCategoryList.add(weightCategoryDTO);

        addCategoryToList(weightCategoryDTO,v,true);

        isWeightListChanged = true;

    }

    private void loadProductData(View view) {

        TextInputEditText name = view.findViewById(R.id.updateProductName);
        Spinner categorySpinner = view.findViewById(R.id.spinner2);
        Spinner availabilitySpinner = view.findViewById(R.id.spinner4);

        // name
        name.setText(productDTO.getName());

        // load category spices, grind spices , seasoning
        List<String> categoryList = new ArrayList<>();
        categoryList.add(getString(R.string.spices));
        categoryList.add(getString(R.string.grind_spices));
        categoryList.add(getString(R.string.seasoning));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryList
        );
        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setSelection(arrayAdapter.getPosition(productDTO.getCategory()));

        // load availability
        List<String> availabilityList = new ArrayList<>();
        availabilityList.add(getString(R.string.available));
        availabilityList.add(getString(R.string.not_available));

        ArrayAdapter<String> availabilityArrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                availabilityList
        );
        availabilitySpinner.setAdapter(availabilityArrayAdapter);
        availabilitySpinner.setSelection(arrayAdapter.getPosition(productDTO.getStock()));

        // weight list
        weightCategoryList = productDTO.getWeight_category();

        for (WeightCategoryDTO weightCategoryDTO:weightCategoryList){

            addCategoryToList(weightCategoryDTO,view,false);

        }

    }

    private void addCategoryToList(WeightCategoryDTO weightCategoryDTO, View view,boolean toStart) {

        ScrollView scrollView = view.findViewById(R.id.scrollView5);
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());

        View inflated = layoutInflater.inflate(R.layout.product_update_weight_category, weightCategoryRecyclerView, false);

        TextView weight = inflated.findViewById(R.id.textView35);
        TextView price = inflated.findViewById(R.id.textView36);
        ImageButton deleteBtn = inflated.findViewById(R.id.imageButton);

        weight.setText(String.valueOf(weightCategoryDTO.getWeight()));
        price.setText(String.valueOf(weightCategoryDTO.getUnit_price()));

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                weightCategoryList.remove(weightCategoryDTO);
                scrollView.removeView(inflated);

                isWeightListChanged = true;

            }
        });

        if(toStart){
            scrollView.addView(inflated,0);
        }else{
            scrollView.addView(inflated);
        }

    }

    private void updateProduct(Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product")
                .document(productDTO.getId())
                .update(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        ProductDTO newProductDTO = new ProductDTO();
                        newProductDTO.setId(productDTO.getId());
                        newProductDTO.setName(updateMap.get("name").toString());
                        newProductDTO.setCategory(updateMap.get("category").toString());
                        newProductDTO.setStock(updateMap.get("stock").toString());
                        newProductDTO.setWeight_category(weightCategoryList);

                        productDTOList.remove(productDTO);
                        productDTOList.add(newProductDTO);

                        productRecyclerViewAdapter.notifyDataSetChanged();
                        weightCategoryRecyclerView.getAdapter().notifyDataSetChanged();

                        Toast.makeText(context,R.string.update_success, Toast.LENGTH_SHORT).show();

                        getParentFragmentManager().popBackStack();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context,R.string.update_failed, Toast.LENGTH_SHORT).show();

                    }
                });


    }
}