package com.example.winlowcustomer;

import static com.example.winlowcustomer.modal.SetUpLanguage.setAppLanguage;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import android.widget.Toast;

import com.example.winlowcustomer.dto.BannerDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.example.winlowcustomer.modal.MainLoadData;
import com.example.winlowcustomer.modal.NetworkConnection;
import com.example.winlowcustomer.modal.SQLiteHelper;
import com.example.winlowcustomer.modal.callback.GetDataCallback;
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
import java.util.HashSet;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static int sqliteVersion = 2;

    private static boolean isDataLoadingFinished;
    private ArrayList<ProductDTO> productDTOArrayList =new ArrayList<>();
    private ArrayList<BannerDTO> bannerArrayList = new ArrayList<>();
    private HashSet<String> categoryHashSet = new HashSet<>();
    public static String language;


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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        language = sharedPreferences.getString("language", "");

        if(!language.isEmpty()){
            setAppLanguage(MainActivity.this, language);
        }

        TextView textView = findViewById(R.id.welcometxt);
        String animateText = getString(R.string.welcome_animation_text);

        // check network connection
        NetworkConnection.register(getApplicationContext());


        isDataLoadingFinished = MainLoadData.mainLoadData(productDTOArrayList, bannerArrayList, categoryHashSet, isDataLoadingFinished, this);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_animation_fade_up);
        ImageView imageView = findViewById(R.id.imageView15);
        imageView.setAnimation(animation);
        animateEachCharacter(textView, animateText);

    }

    private void navigateToNextActivity() {


        Intent intent;
        if (language.isBlank()) {
            intent = new Intent(MainActivity.this, WelcomeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, HomeActivity.class);

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
                        } else {
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