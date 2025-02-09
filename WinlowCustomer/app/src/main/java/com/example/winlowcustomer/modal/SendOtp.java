package com.example.winlowcustomer.modal;

import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendOtp {

    public static String generateOTP(int length) {

        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Appends a random digit (0-9)
        }

        return otp.toString();
    }

    public static String send(String sendTo) {

        String otp = generateOTP(6);

        String userId = "29036";
        String apikey = "4NItv0q27kXDC0SZI46w";
        String senderId = "NotifyDEMO";
        String to = sendTo.replaceFirst("^0", "+94");
        String message = "Your OTP is: " + otp;

        String stringurl = "https://app.notify.lk/api/v1/send?user_id=" + userId + "&api_key=" + apikey + "&sender_id=" + senderId + "&to=" + to + "&message=" + message;

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient httpClient = new OkHttpClient();
                Request request = new Request.Builder().url(stringurl).build();

                try{
                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful() && response.code() == 200) {

                        Log.i("OTP", "OTP sent successfully");

                    }else{

                        Log.i("OTP", "OTP not sent");

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

        return otp;
    }
}
