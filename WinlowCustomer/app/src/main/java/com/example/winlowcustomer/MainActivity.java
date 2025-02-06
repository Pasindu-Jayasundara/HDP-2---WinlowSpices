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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private boolean isDataLoadingFinished;

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

        new Thread(new Runnable() {
            @Override
            public void run() {

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("product")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){

                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                    for(DocumentSnapshot document : documents){

                                        document.getId()

                                    }

                                }else{
                                    // load from sqlite
                                }

                            }
                        });

            }
        }).start();

        animateEachCharacter(textView, animateText);

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