package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.MainActivity.sqliteVersion;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.callback.GetDataCallback;
import com.example.winlowcustomer.modal.callback.MainLoadDataCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainLoadData {

    public static boolean isProductLoadingDone;
    public static boolean isBannerLoadingDone;

    public static void mainLoadData(ArrayList<ProductDTO> productDTOArrayList, ArrayList<BannerDTO> bannerArrayList, HashSet<String> categoryHashSet, Context context, MainLoadDataCallback mainLoadDataCallback) {

        Gson gson = new Gson();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // products
        firestore.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            if(!categoryHashSet.isEmpty()){
                                categoryHashSet.clear();
                            }
                            if(!productDTOArrayList.isEmpty()){
                                productDTOArrayList.clear();
                            }

                            boolean isFirstTime = true;

                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {

                                List<Object> weightCategoryRawList = (List<Object>) document.get("weight_category");
                                String weightCategoryJson = gson.toJson(weightCategoryRawList);

                                Log.i("xyz", weightCategoryJson);

                                Type listType = new TypeToken<List<WeightCategoryDTO>>() {
                                }.getType();
                                List<WeightCategoryDTO> weightCategoryDTOList = gson.fromJson(weightCategoryJson, listType);
                                for(WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList){
                                    Log.i("xyz","2: "+ gson.toJson(weightCategoryDTO));

                                }

                                if(isFirstTime){
                                    categoryHashSet.add("All");
                                    isFirstTime = false;
                                }
                                categoryHashSet.add(document.getString("category"));
                                productDTOArrayList.add(
                                        new ProductDTO(
                                                document.getId(),
                                                document.getString("category"),
                                                document.getString("name"),
                                                document.getString("stock"),
                                                document.getDouble("discount"),
                                                weightCategoryDTOList,
                                                document.getString("image_path"),
                                                document.getReference().getPath()
                                        )
                                );

                            }

                            SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            String categoryJson = gson.toJson(categoryHashSet);
                            String productJson = gson.toJson(productDTOArrayList);
                            Log.i("xyz", productJson);

                            editor.putString("category", categoryJson);
                            editor.putString("product", productJson);
                            editor.commit();

                        }

                        if (productDTOArrayList.isEmpty()) {
                            // load recently viewed products

                            SQLiteHelper helper = new SQLiteHelper(context.getApplicationContext(), "winlow.db", null, sqliteVersion);
                            helper.getRecentlyViewedProduct(helper, new GetDataCallback() {
                                @Override
                                public void onGetData(Cursor cursor) {

                                    while (cursor.moveToNext()) {

                                        String name = cursor.getString(0);
                                        String stock = cursor.getString(1);
                                        String docId = cursor.getString(3);
                                        String discount = cursor.getString(4);
                                        String imagePath = cursor.getString(5);


                                        ProductDTO productDTO = new ProductDTO();
                                        productDTO.setId(docId);
                                        productDTO.setName(name);
                                        productDTO.setStock(stock);
                                        productDTO.setDiscount(Double.parseDouble(discount));
                                        productDTO.setWeight_category(new ArrayList<>());
                                        productDTO.setImage_path(imagePath);

                                        productDTOArrayList.add(productDTO);

                                    }

                                    isProductLoadingDone = true;
                                    goBack(mainLoadDataCallback);

                                }
                            });

                        }else{
                            isProductLoadingDone = true;
                            goBack(mainLoadDataCallback);
                        }

                    }
                });

        // banners & category
        firestore.collection("banner")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            if(!bannerArrayList.isEmpty()){
                                bannerArrayList.clear();
                            }

                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {

                                List<Object> bannerPathRawList = (List<Object>) document.get("path");
                                String bannerPathJson = gson.toJson(bannerPathRawList);

                                Type listType = new TypeToken<ArrayList<String>>() {
                                }.getType();

                                List<String> bannerList = gson.fromJson(bannerPathJson, listType);

                                BannerDTO bannerDTO = new BannerDTO();
                                bannerDTO.setImagePathList(bannerList);
                                bannerArrayList.add(bannerDTO);

                            }

                            SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            String bannerString = gson.toJson(bannerArrayList);

                            editor.putString("banner", bannerString);
                            editor.commit();

                        }
                        isBannerLoadingDone = true;
                        goBack(mainLoadDataCallback);
                    }
                });


    }

    private static void goBack(MainLoadDataCallback mainLoadDataCallback) {

        if(isBannerLoadingDone && isProductLoadingDone){
            mainLoadDataCallback.onMainLoadData(true);
        }

    }


}
