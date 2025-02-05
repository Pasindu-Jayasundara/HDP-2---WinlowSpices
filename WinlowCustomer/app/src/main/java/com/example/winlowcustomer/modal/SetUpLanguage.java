package com.example.winlowcustomer.modal;

import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;

public class SetUpLanguage {

    public static void setAppLanguage(String languageCode, Context context) {
        // Create a Locale object based on the language code
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Update the configuration with the new locale
        Configuration config = new Configuration();
        config.setLocale(locale);

        // Apply the new configuration to the current app context
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

}
