package com.example.winlowcustomer.modal;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;
import static com.example.winlowcustomer.MainActivity.language;

import com.example.winlowcustomer.modal.callback.GetCompleteCallback;

public class SetUpLanguage {

    public static void setAppLanguage(Activity activity,String langCode) {



//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
//        String languageCode = sharedPreferences.getString("language", "");
        language = langCode;
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

    public static void setAppLanguage(Activity activity,String langCode, GetCompleteCallback getCompleteCallback) {

        setAppLanguage(activity,langCode);

        getCompleteCallback.onComplete();

    }

}
