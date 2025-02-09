package com.example.winlowcustomer.modal;

import android.content.Context;
import android.widget.Toast;

import com.example.winlowcustomer.R;

import java.util.regex.Pattern;

public class Verify {

    private static final String MOBILE_NUMBER_REGEX = "^(07|\\+947)([1245678]\\d{7})$";

    public static boolean verifyMobileNumber(String mobile) {
        return Pattern.compile(MOBILE_NUMBER_REGEX).matcher(mobile).matches();
    }

    public static boolean verifyMobileNumber(String mobileNumber, Context context){

        if(mobileNumber.trim().isEmpty()){

            Toast.makeText(context, R.string.invalid_mobile_number_missing,Toast.LENGTH_LONG).show();
            return false;
        }

        if(mobileNumber.length() > 10){

            Toast.makeText(context, R.string.invalid_mobile_number_length,Toast.LENGTH_LONG).show();
            return false;
        }

        if(!mobileNumber.startsWith("07")){

            Toast.makeText(context, R.string.invalid_mobile_number_format,Toast.LENGTH_LONG).show();
            return false;
        }

        if(verifyMobileNumber(mobileNumber)){

            return true;
        }else{

            Toast.makeText(context, R.string.invalid_mobile_number,Toast.LENGTH_LONG).show();

            return false;
        }

    }

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(gmail\\.com|yahoo\\.com|hotmail\\.com)$";

    public static boolean verifyEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }
}
