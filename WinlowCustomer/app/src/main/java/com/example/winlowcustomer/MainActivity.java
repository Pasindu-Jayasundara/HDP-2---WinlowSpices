package com.example.winlowcustomer;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

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

        TextView textView = findViewById(R.id.welcometxt);
        String animateText = getString(R.string.welcome_animation_text);

        animateEachCharacter(textView, animateText);

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

                        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
                        String language = sharedPreferences.getString("language", "");

                        Intent intent;
                        if (language.isBlank()) {
                            intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, HomeActivity.class);

                            setAppLanguage(language);

                        }
                        startActivity(intent);
                        finish();

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

    private void setAppLanguage(String languageCode) {
        // Create a Locale object based on the language code
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Update the configuration with the new locale
        Configuration config = new Configuration();
        config.setLocale(locale);

        // Apply the new configuration to the current app context
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}