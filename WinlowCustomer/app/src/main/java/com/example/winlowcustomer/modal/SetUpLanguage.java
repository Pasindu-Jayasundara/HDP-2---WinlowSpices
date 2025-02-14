package com.example.winlowcustomer.modal;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class SetUpLanguage {

    public static void setAppLanguage(Activity activity,String langCode) {



//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
//        String languageCode = sharedPreferences.getString("language", "");
//
//        if(!languageCode.isBlank()){
            Locale locale = new Locale(langCode);
            Locale.setDefault(locale);

        Resources resources = activity.getResources();

            Configuration config =resources.getConfiguration();
            config.setLocale(locale);

            resources.updateConfiguration(config,resources.getDisplayMetrics());

//            context.getResources().updateConfiguration(config,
//                    context.getResources().getDisplayMetrics());
//        }

    }

}
