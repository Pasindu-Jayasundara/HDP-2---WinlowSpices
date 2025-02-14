package com.example.winlowcustomer;

import static com.example.winlowcustomer.modal.SetUpLanguage.setAppLanguage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button englishButton = findViewById(R.id.button);
        Button sinhalaButton = findViewById(R.id.button2);

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveLanguage("en");
            }
        });

        sinhalaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveLanguage("si");
            }
        });
    }

    private void saveLanguage(String language) {

        SharedPreferences.Editor editor = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.apply();

        setAppLanguage(WelcomeActivity.this, language);
//        finish();
//        startActivity(getIntent());

        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }
}