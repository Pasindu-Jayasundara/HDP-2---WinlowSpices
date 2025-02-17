package com.example.winloadmin.product;

import static android.app.Activity.RESULT_OK;

import static com.example.winloadmin.MainActivity.productDTOList;
import static com.example.winloadmin.nav.DashboardFragment.topSellingProduct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import com.example.winloadmin.R;
import com.example.winloadmin.dto.ProductDTO;
import com.example.winloadmin.dto.WeightCategoryDTO;
import com.example.winloadmin.model.callback.ImageUploadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewProductFragment extends Fragment {

    Map<String, Object> productMap = new HashMap<>();
    ImageView productImg;
    List<WeightCategoryDTO> weightCategoryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productImg = view.findViewById(R.id.imageView5);
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProductImage();
            }
        });

        // add category btn
        Button addBtn = view.findViewById(R.id.button10);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWeightList(view);
            }
        });

        // save btn
        Button btn = view.findViewById(R.id.button11);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isOk = checkAbility(view);
                if (isOk) {
                    saveProduct(view);
                } else {
                    Toast.makeText(v.getContext(), R.string.missing_data, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // load categories
        Spinner categorySpinner = view.findViewById(R.id.spinner5);
        Spinner availabilitySpinner = view.findViewById(R.id.spinner6);

        // load category spices, grind spices , seasoning
        List<String> categoryList = new ArrayList<>();
        categoryList.add(getString(R.string.select_category));
        categoryList.add(getString(R.string.spices));
        categoryList.add(getString(R.string.grind_spices));
        categoryList.add(getString(R.string.seasoning));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryList
        );
        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setSelection(0);

        // load availability
        List<String> availabilityList = new ArrayList<>();
        availabilityList.add(getString(R.string.select_availability));
        availabilityList.add(getString(R.string.available));
        availabilityList.add(getString(R.string.not_available));

        ArrayAdapter<String> availabilityArrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                availabilityList
        );
        availabilitySpinner.setAdapter(availabilityArrayAdapter);
        availabilitySpinner.setSelection(0);

    }

    private void addToWeightList(View v) {

        // new weight
        EditText weightView = v.findViewById(R.id.editTextText6);
        String weight = weightView.getText().toString();
        if (weight.isBlank()) {
            Toast.makeText(v.getContext(), R.string.missing_weight, Toast.LENGTH_SHORT).show();
            return;
        }

        // new price
        EditText priceView = v.findViewById(R.id.editTextText5);
        String price = priceView.getText().toString();
        if (price.isBlank()) {
            Toast.makeText(v.getContext(), R.string.missing_price, Toast.LENGTH_SHORT).show();
            return;
        }

        WeightCategoryDTO weightCategoryDTO = new WeightCategoryDTO();
        weightCategoryDTO.setWeight(Double.parseDouble(weight));
        weightCategoryDTO.setUnit_price(Double.parseDouble(price));

        weightCategoryList.add(weightCategoryDTO);

        addCategoryToList(weightCategoryDTO, v);

        weightView.setText("");
        priceView.setText("");

    }

    private void addCategoryToList(WeightCategoryDTO weightCategoryDTO, View view) {

        ScrollView scrollView = view.findViewById(R.id.weightScrollView);
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());

        ConstraintLayout constraintLayout = view.findViewById(R.id.frameLayout9);
        View inflated = layoutInflater.inflate(R.layout.product_update_weight_category, constraintLayout, false);

        TextView weight = inflated.findViewById(R.id.textView35);
        TextView price = inflated.findViewById(R.id.textView36);
        ImageButton deleteBtn = inflated.findViewById(R.id.imageButton);

        weight.setText(String.valueOf(weightCategoryDTO.getWeight())+" g");
        price.setText("Rs. "+String.valueOf(weightCategoryDTO.getUnit_price()));

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                weightCategoryList.remove(weightCategoryDTO);

                // Find the parent layout (LinearLayout) that holds all items
                LinearLayout parentLayout = (LinearLayout) scrollView.getChildAt(0);
                parentLayout.removeView(inflated);  // Remove the inflated item view

                scrollView.removeView(inflated);

            }
        });

//        scrollView.addView(inflated, 0);

        // Get the parent layout (LinearLayout) inside the ScrollView
        LinearLayout parentLayout;
        if (scrollView.getChildCount() == 0) {
            // Create the parent layout if it doesn't exist
            parentLayout = new LinearLayout(view.getContext());
            parentLayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(parentLayout);  // Add the LinearLayout as the only child of ScrollView
        } else {
            parentLayout = (LinearLayout) scrollView.getChildAt(0);  // Get the existing parent layout
        }

        // Add the inflated item to the parent layout (LinearLayout)
            parentLayout.addView(inflated, 0);  // Add to the start

    }

    private boolean checkAbility(View view) {

        boolean isOk = false;

        TextInputEditText nameView = view.findViewById(R.id.newProductName);
        Spinner categoryView = view.findViewById(R.id.spinner5);
        Spinner availabilityView = view.findViewById(R.id.spinner6);

        String name = nameView.getText().toString();

        long category = categoryView.getSelectedItemId();
        long availability = availabilityView.getSelectedItemId();

        String imagePath = productMap.get("image_path").toString();

        if (!name.isBlank() && category!=0 &&
                availability!=0 && !weightCategoryList.isEmpty() &&
                !imagePath.isBlank()) {

            String newCategory = categoryView.getSelectedItem().toString();
            String newAvailability = availabilityView.getSelectedItem().toString();

            productMap.put("category", newCategory);
            productMap.put("name", name);
            productMap.put("stock", newAvailability);
            productMap.put("weight_category", weightCategoryList);
            productMap.put("discount", 0);

            isOk = true;
        }

        return isOk;

    }

    private void selectProductImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Log.i("vta", new Gson().toJson(data));

            Uri imageUri = data.getData();

            productImg.setImageURI(imageUri);

            productMap.put("image_path", String.valueOf(imageUri));

        }

    }

    private void imageUpload(ImageUploadCallback imageUploadCallback) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference firebaseStorageReference = firebaseStorage.getReference();

        StorageReference fileRef = firebaseStorageReference.child("productImage/" + System.currentTimeMillis() + ".jpg");
        fileRef.putFile(Uri.parse(productMap.get("image_path").toString()))
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            productMap.put("image_path", uri.toString());
                            imageUploadCallback.imageUpload(true);

                        })
                        .addOnFailureListener(e -> {
                            imageUploadCallback.imageUpload(false);
                        })
                )
                .addOnFailureListener(e -> {
                    imageUploadCallback.imageUpload(false);
                    productImg.setImageResource(R.drawable.product_image);
                });


    }

    private void saveProduct(View view) {

        imageUpload(new ImageUploadCallback() {
            @Override
            public void imageUpload(boolean isUploaded) {

                if(isUploaded){

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("product")
                            .add(productMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {


                                    Gson gson = new Gson();
                                    String json = gson.toJson(productMap);

                                    ProductDTO productDTO = gson.fromJson(json, ProductDTO.class);
                                    productDTOList.add(productDTO);

//                                    RecyclerView recyclerView = view.getRootView().findViewById(R.id.productRecyclerView);
//                                    recyclerView.getAdapter().notifyDataSetChanged();

//                                    RecyclerView recyclerView2 = view.getRootView().findViewById(R.id.allProductRecyclerView);
//                                    recyclerView2.getAdapter().notifyDataSetChanged();

                                    // top selling product name
//                                    TextView topSellingProductView = view.getRootView().findViewById(R.id.textView5);
//                                    topSellingProductView.setText(topSellingProduct);

                                    // product count
//                                    TextView productCount = view.getRootView().findViewById(R.id.textView3);
//                                    productCount.setText(String.valueOf(productDTOList.size()));

                                    Toast.makeText(view.getContext(), R.string.product_adding_success, Toast.LENGTH_SHORT).show();
                                    clearFields(view);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(view.getContext(), R.string.product_adding_failed, Toast.LENGTH_SHORT).show();
                                    deleteImage(view);

                                }
                            });

                }else{
                    Toast.makeText(view.getContext(), R.string.image_uploadFailed_abort_adding, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void clearFields(View view) {

        TextInputEditText nameView = view.findViewById(R.id.newProductName);
        Spinner categoryView = view.findViewById(R.id.spinner5);
        Spinner availabilityView = view.findViewById(R.id.spinner6);
        ScrollView scrollView = view.findViewById(R.id.weightScrollView);

        nameView.setText("");
        categoryView.setSelection(0);
        availabilityView.setSelection(0);

        productImg.setImageResource(R.drawable.product_image);
        weightCategoryList.clear();
        scrollView.removeAllViews();

        EditText weightView = view.findViewById(R.id.editTextText6);
        EditText priceView = view.findViewById(R.id.editTextText5);

        weightView.setText("");
        priceView.setText("");

    }

    private void deleteImage(View view) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference existingFileRef = firebaseStorage.getReferenceFromUrl(productMap.get("image_path").toString());

        // Delete the existing file
        existingFileRef.delete()
                .addOnSuccessListener(aVoid -> {
                    clearFields(view);
                })
                .addOnFailureListener(e -> {
                    // Handle failure in deleting the existing image
                    Toast.makeText(view.getContext(), R.string.failed_to_delete_old_image, Toast.LENGTH_SHORT).show();
                    clearFields(view);
                });

    }

}