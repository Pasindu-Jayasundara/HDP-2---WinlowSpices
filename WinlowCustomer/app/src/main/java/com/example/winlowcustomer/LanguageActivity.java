package com.example.winlowcustomer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.modal.SetUpLanguage;

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

        if(!language.isBlank()){

            if(language.equals("en")){
                radioGroup.check(R.id.radioButton);
            }else if(language.equals("si")){
                radioGroup.check(R.id.radioButton2);
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

                if(checkedId == R.id.radioButton){
                    SetUpLanguage.setAppLanguage(LanguageActivity.this, "en");
                }else if(checkedId == R.id.radioButton2){
                    SetUpLanguage.setAppLanguage(LanguageActivity.this, "si");
                }

                recreate();

            }
        });

    }
}