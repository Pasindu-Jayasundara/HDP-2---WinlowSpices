package com.example.winlowcustomer;

import static com.example.winlowcustomer.modal.SetUpLanguage.setAppLanguage;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.animation.ValueAnimator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import android.graphics.Color;

import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.NetworkConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private boolean isDataLoadingFinished;
    public static HashMap<String,Object> productHashMap;
    public static ArrayList<BannerDTO> bannerArrayList;
    public static ArrayList<String> categoryList;

    private static final String ALL = "all", PRODUCTS = "products", BANNERS = "banners", CATEGORY = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);



        TextView textView = findViewById(R.id.welcometxt);
        String animateText = getString(R.string.welcome_animation_text);

        if(!NetworkConnection.hasConnection){
            loadFromSQLite(MainActivity.ALL);
        }else{
            loadFromFirebase();
        }


        animateEachCharacter(textView, animateText);

    }

    private void loadFromFirebase(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                Gson gson = new Gson();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                // products
                firestore.collection("product")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                productHashMap = new HashMap<>();
                                categoryList = new ArrayList<>();

                                if(task.isSuccessful()){

                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                    for(DocumentSnapshot document : documents){

                                        List<Object> weightCategoryRawList = (List<Object>) document.get("weightCategory");
                                        String weightCategoryJson = gson.toJson(weightCategoryRawList);

                                        Type listType = new TypeToken<List<WeightCategoryDTO>>() {}.getType();
                                        List<WeightCategoryDTO> weightCategoryDTOList = gson.fromJson(weightCategoryJson, listType);

                                        categoryList.add(document.getString("category"));
                                        productHashMap.put(
                                                document.getId(),
                                                new ProductDTO(
                                                        document.getId(),
                                                        document.getString("category"),
                                                        document.getString("name"),
                                                        document.getString("stock"),
                                                        document.getDouble("discount"),
                                                        weightCategoryDTOList
                                                )
                                        );

                                    }

                                }else{
                                    // load from sqlite
                                    loadFromSQLite(MainActivity.PRODUCTS);
                                }

                            }
                        });

                // banners & category
                firestore.collection("banner")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){

                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                    for(DocumentSnapshot document : documents){

                                        List<Object> bannerPathRawList = (List<Object>) document.get("path");
                                        String bannerPathJson = gson.toJson(bannerPathRawList);

                                        Type listType = new TypeToken<List<BannerDTO>>() {}.getType();
                                        List<BannerDTO> bannerDTOList = gson.fromJson(bannerPathJson, listType);
                                        bannerArrayList = (ArrayList<BannerDTO>) bannerDTOList;

                                    }

                                }else{
                                    // load from sqlite
                                    loadFromSQLite(MainActivity.BANNERS);
                                    loadFromSQLite(MainActivity.CATEGORY);
                                }

                            }
                        });

                isDataLoadingFinished = true;

            }
        }).start();
    }

    private void loadFromSQLite(String whichOne){
        // load from sqlite

        if(whichOne.equals(MainActivity.ALL)){
            loadFromSQLite(MainActivity.PRODUCTS);
            loadFromSQLite(MainActivity.BANNERS);
            loadFromSQLite(MainActivity.CATEGORY);
        }

        if(whichOne.equals(MainActivity.PRODUCTS)){
            // load product
        }

        if(whichOne.equals(MainActivity.BANNERS)){
            // load banners
        }

        if(whichOne.equals(MainActivity.CATEGORY)){
            // load category
        }

        isDataLoadingFinished = true;
    }

    private void navigateToNextActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");

        Intent intent;
        if (language.isBlank()) {
            intent = new Intent(MainActivity.this, WelcomeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, HomeActivity.class);

            setAppLanguage(language, getApplicationContext());

        }
        startActivity(intent);
        finish();
    }

    private void animateEachCharacter(TextView textView, String text) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        textView.setText(spannable, TextView.BufferType.SPANNABLE);
        textView.setTextColor(getColor(R.color.transparent));

        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            ValueAnimator animator = ValueAnimator.ofArgb(Color.TRANSPARENT, Color.BLACK);
            animator.setDuration(300);
            animator.setStartDelay(100 * i);
            animator.addUpdateListener(animation -> {
                int animatedColor = (int) animation.getAnimatedValue();
                spannable.setSpan(new ForegroundColorSpan(animatedColor), index, index + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannable);
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {

                    if (index == text.length() - 1) {

                        if (isDataLoadingFinished) {
                            navigateToNextActivity();
                        }else{
                            animateEachCharacter(textView, text);
                        }

                    }

                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
            animator.start();
        }
    }

}