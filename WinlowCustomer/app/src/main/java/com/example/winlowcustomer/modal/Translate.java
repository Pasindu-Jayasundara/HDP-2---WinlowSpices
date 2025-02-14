package com.example.winlowcustomer.modal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.winlowcustomer.modal.callback.TranslationCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Translate {

    private static final String API_KEY = "AIzaSyD99JX7lNhk7HmldiI7ZoNmMcTrp3AUEPc";  // Replace with your DeepL API Key
    private static final String API_URL = "https://translation.googleapis.com/language/translate/v2";

    public static void translateText(String text, String targetLang, TranslationCallback callback) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("q", text)
                .addQueryParameter("target", targetLang)
                .addQueryParameter("key", API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {

            // Inside onResponse
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        JSONArray translations = jsonResponse.getJSONObject("data").getJSONArray("translations");
                        String translatedText = translations.getJSONObject(0).getString("translatedText");

                        // Run UI update on the Main Thread
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(translatedText));

                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Parsing Error: " + e.getMessage()));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("API Error: " + response.message()));
                }
            }

            // Inside onFailure
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Request Failed: " + e.getMessage()));
            }

        });
    }

}

