package com.example.winlowcustomer.modal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

public class SetUpLanguage {

    public static void setAppLanguage(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        String languageCode = sharedPreferences.getString("language", "");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

}
