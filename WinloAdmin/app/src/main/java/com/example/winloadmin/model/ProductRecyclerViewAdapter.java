package com.example.winloadmin.model;

import static com.example.winloadmin.MainActivity.fragmentManager;
import static com.example.winloadmin.MainActivity.orderDTOList;
import static com.example.winloadmin.MainActivity.productDTOList;
import static com.example.winloadmin.product.ProductSearchFragment.recyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.dto.OrderItemDTO;
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

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductRecyclerViewHolder>{

    String productId;
    List<WeightCategoryDTO> weightCategoryList;
    boolean isWeightListChanged;
    Map<String,Object> updateMap = new HashMap<>();
    List<ProductDTO> filteredList;

    public ProductRecyclerViewAdapter(List<ProductDTO> filteredList) {
        this.filteredList = filteredList;
    }
//    RecyclerView weightCategoryRecyclerView;

    @NonNull
    @Override
    public ProductRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_search_recyclerview_card,parent,false);

        return new ProductRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerViewHolder holder, int position) {

        ProductDTO productDTO = filteredList.get(position);

        holder.productName.setText(productDTO.getName());
        holder.category.setText(productDTO.getCategory());

        // image
        Glide.with(holder.itemView.getContext())
                .load(productDTO.getImage_path())
                .placeholder(R.drawable.product_image)
                .error(R.drawable.product_image)
                .into(holder.productImage);

        // delete btn
        productId = productDTO.getId();
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_txt)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean isAbleToDelete = checkDeleteAbility(productDTO);
                                if(isAbleToDelete){
                                    deleteProduct(productDTO,v.getContext());
                                }else{
                                    Toast.makeText(v.getContext(),R.string.cannot_be_deleted,Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();

            }
        });

        // update btn
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View inflated = layoutInflater.inflate(R.layout.update_product, v.findViewById(R.id.productSearchConstraintLayout), false);

//                RecyclerView recyclerView1 = inflated.findViewById(R.id.recyclerViewUpdateProduct);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false);
//                recyclerView1.setLayoutManager(linearLayoutManager);

                weightCategoryList = productDTO.getWeight_category();
//                recyclerView1.setAdapter(new );


                loadData(inflated,v,productDTO);

//                weightCategoryRecyclerView = inflated.findViewById(R.id.recyclerViewUpdateProduct);

                // new weight
                EditText newWeightView = inflated.findViewById(R.id.editTextText);
                EditText newPriceView = inflated.findViewById(R.id.editTextText2);
                Button addBtn = inflated.findViewById(R.id.button8);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToWeightList(newWeightView.getText().toString(),newPriceView.getText().toString(),v,inflated);
                    }
                });

                // save updates
                Button saveBtn = inflated.findViewById(R.id.button9);
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isChanged = isChanged(productDTO);
                        if(isChanged){
                            updateProduct(v.getContext(),productDTO);
                        }else{
                            Toast.makeText(v.getContext(),R.string.no_changes,Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                new AlertDialog.Builder(v.getContext())
                            .setView(inflated)
                            .show();

//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragmentContainerView3,new UpdateProductFragment(productDTO,holder.recyclerView))
//                        .setReorderingAllowed(true)
//                        .commit();

            }
        });

        // recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.VERTICAL,false);

        holder.recyclerView.setAdapter(new WeightCategoryRecyclerViewAdapter(productDTO.getWeight_category()));
        holder.recyclerView.setLayoutManager(linearLayoutManager);

    }

    private void updateProduct(Context context,ProductDTO productDTO) {

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

                        filteredList.remove(productDTO);
                        filteredList.add(newProductDTO);
                        productDTOList.add(newProductDTO);
                        productDTOList.add(newProductDTO);

                        recyclerView.getAdapter().notifyDataSetChanged();
//                        productRecyclerViewAdapter.notifyDataSetChanged();
//                        weightCategoryRecyclerView.getAdapter().notifyDataSetChanged();

                        Toast.makeText(context,R.string.update_success, Toast.LENGTH_SHORT).show();

                        fragmentManager.popBackStack();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context,R.string.update_failed, Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private boolean isChanged(ProductDTO productDTO) {

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

    private void addToWeightList(String newWeight, String newPrice, View v,View inflated) {

        WeightCategoryDTO weightCategoryDTO = new WeightCategoryDTO();
        weightCategoryDTO.setWeight(Integer.parseInt(newWeight));
        weightCategoryDTO.setUnit_price(Integer.parseInt(newPrice));

        weightCategoryList.add(weightCategoryDTO);

        addCategoryToList(weightCategoryDTO,v,true, inflated);

        isWeightListChanged = true;

    }

    private void loadData(View inflated, View v,ProductDTO productDTO) {

        TextInputEditText name = inflated.findViewById(R.id.updateProductName);
        Spinner categorySpinner = inflated.findViewById(R.id.spinner2);
        Spinner availabilitySpinner = inflated.findViewById(R.id.spinner4);
//        RecyclerView recyclerView1 = inflated.findViewById(R.id.recyclerViewUpdateProduct);

        // name
        name.setText(productDTO.getName());

        // load category spices, grind spices , seasoning
        List<String> categoryList = new ArrayList<>();
        categoryList.add(v.getContext().getString(R.string.spices));
        categoryList.add(v.getContext().getString(R.string.grind_spices));
        categoryList.add(v.getContext().getString(R.string.seasoning));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                v.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryList
        );
        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setSelection(arrayAdapter.getPosition(productDTO.getCategory()));

        // load availability
        List<String> availabilityList = new ArrayList<>();
        availabilityList.add(v.getContext().getString(R.string.available));
        availabilityList.add(v.getContext().getString(R.string.not_available));

        ArrayAdapter<String> availabilityArrayAdapter = new ArrayAdapter<>(
                v.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                availabilityList
        );
        availabilitySpinner.setAdapter(availabilityArrayAdapter);
        availabilitySpinner.setSelection(arrayAdapter.getPosition(productDTO.getStock()));

        // weight list
        weightCategoryList = productDTO.getWeight_category();



        for (WeightCategoryDTO weightCategoryDTO:weightCategoryList){

            addCategoryToList(weightCategoryDTO,v,false,inflated);

        }

    }

    private void addCategoryToList(WeightCategoryDTO weightCategoryDTO, View view, boolean toStart, View inflated) {


        ScrollView scrollView = inflated.findViewById(R.id.scrollView5);

        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View inflatedWeightView = layoutInflater.inflate(R.layout.product_update_weight_category, null, false);

        TextView weight = inflatedWeightView.findViewById(R.id.textView35);
        TextView price = inflatedWeightView.findViewById(R.id.textView36);
        ImageButton deleteBtn = inflatedWeightView.findViewById(R.id.imageButton);

        weight.setText(String.valueOf(weightCategoryDTO.getWeight())+" g");
        price.setText("Rs. "+String.valueOf(weightCategoryDTO.getUnit_price()));

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightCategoryList.remove(weightCategoryDTO);

//                recyclerView1.getAdapter().notifyDataSetChanged();
//                // Find the parent layout (LinearLayout) that holds all items
                LinearLayout parentLayout = (LinearLayout) scrollView.getChildAt(0);
                parentLayout.removeView(inflatedWeightView);  // Remove the inflated item view

                isWeightListChanged = true;
            }
        });

//        weightCategoryList.add(weightCategoryDTO);

        // Get the parent layout (LinearLayout) inside the ScrollView
        LinearLayout parentLayout;
        if (scrollView.getChildCount() == 0) {
//            // Create the parent layout if it doesn't exist
            parentLayout = new LinearLayout(view.getContext());
            parentLayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(parentLayout);  // Add the LinearLayout as the only child of ScrollView
        } else {
            parentLayout = (LinearLayout) scrollView.getChildAt(0);  // Get the existing parent layout
        }

        // Add the inflated item to the parent layout (LinearLayout)
        if (toStart) {
            parentLayout.addView(inflatedWeightView, 0);  // Add to the start
        } else {
            parentLayout.addView(inflatedWeightView);  // Add to the end
        }
    }

    private boolean checkDeleteAbility(ProductDTO deletingProductDTO) {

        boolean isNotFound = true;

        for(OrderDTO orderDTO : orderDTOList){

            List<OrderItemDTO> orderList = orderDTO.getOrder_list();
            for (OrderItemDTO orderItemDTO:orderList){

                if(orderItemDTO.getId().equals(deletingProductDTO.getId())){

                    isNotFound = false;
                    break;
                }

            }

        }

        return isNotFound;

    }

    private void deleteProduct(ProductDTO productDTO, Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product")
                .document(productId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        filteredList.remove(productDTO);
                        productDTOList.remove(productDTO);
                        notifyDataSetChanged();

                        Toast.makeText(context,R.string.delete_success, Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context,R.string.delete_failed, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ProductRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productName;
        TextView category;
        RecyclerView recyclerView;
        Button deleteBtn;
        Button updateBtn;

        public ProductRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.imageView3);
            productName = itemView.findViewById(R.id.textView7);
            category = itemView.findViewById(R.id.textView8);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            deleteBtn = itemView.findViewById(R.id.button4);
            updateBtn = itemView.findViewById(R.id.button7);
        }
    }
}
