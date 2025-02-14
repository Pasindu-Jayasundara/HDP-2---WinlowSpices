package com.example.winlowcustomer;

import static com.example.winlowcustomer.MainActivity.language;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.callback.GetCompleteCallback;


public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView6, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        //language
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");

        RadioGroup radioGroup = findViewById(R.id.langRadioGroup);
        RadioButton eng = findViewById(R.id.radioButton);
        RadioButton sin = findViewById(R.id.radioButton2);

        if (!language.isBlank()) {

            if (language.equals("en")) {
                sin.setChecked(false);
                eng.setChecked(true);
            } else if (language.equals("si")) {
                eng.setChecked(false);
                sin.setChecked(true);
            }

        }


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // back
        ImageButton back = findViewById(R.id.imageButton12);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // language change
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                String languageCode = "en"; // Default language
                if (checkedId == R.id.radioButton2) {
                    languageCode = "si"; // Change to Sinhala
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", languageCode);
                editor.apply();

                SetUpLanguage.setAppLanguage(LanguageActivity.this, languageCode, new GetCompleteCallback() {
                    @Override
                    public void onComplete() {
                        // Restart the entire application
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        // Kill the process to ensure a fresh start
//                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        SetUpLanguage.setAppLanguage(LanguageActivity.this, language);
    }
}