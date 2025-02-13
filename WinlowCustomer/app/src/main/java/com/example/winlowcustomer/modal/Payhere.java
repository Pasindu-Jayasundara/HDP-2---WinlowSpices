package com.example.winlowcustomer.modal;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;

public class Payhere {

    public static void pay(HashMap<String,Object> data,Activity activity) {

        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("email");
        String mobile = (String) data.get("mobile");
        String address = (String) data.get("address");
        double totalPrice = 0.0;
        String orderId = (String) data.get("orderId");
        List<Item> items = (List<Item>) data.get("items");
//        Activity activity = (Activity) data.get("activity");
        int requestId = (int) data.get("requestId");
        String id = (String) data.get("id");


        String totalPriceStr = String.valueOf(data.get("totalPrice")).replaceAll("[^0-9.]", ""); // Remove non-numeric characters

// Ensure the string starts with a number and contains only one decimal point
        if (totalPriceStr.startsWith(".")) {
            totalPriceStr = totalPriceStr.substring(1); // Remove leading dot
        }
        if (totalPriceStr.contains(".")) {
            int firstDotIndex = totalPriceStr.indexOf(".");
            totalPriceStr = totalPriceStr.substring(0, firstDotIndex + 1) +
                    totalPriceStr.substring(firstDotIndex + 1).replace(".", ""); // Remove extra dots
        }

        try {
            totalPrice = Double.parseDouble(totalPriceStr);
            Log.d("Payhere", "Final totalPrice: " + totalPrice);
        } catch (NumberFormatException e) {
            Log.e("Payhere", "Invalid totalPrice format after cleanup: " + totalPriceStr);
            return;
        }




        InitRequest req = new InitRequest();
        req.setMerchantId("1228237");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(totalPrice);             // Final Amount to be charged
        req.setOrderId(orderId);        // Unique Reference ID
        req.setItemsDescription("Product Purchase");  // Item description title
        req.setCustom1(id);
        req.setCustom2("");
        req.getCustomer().setFirstName(firstName);
        req.getCustomer().setLastName(lastName);
        req.getCustomer().setEmail(email);
        req.getCustomer().setPhone(mobile);
        req.getCustomer().getAddress().setAddress(address);
//        req.setSandBox(true);
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        //Optional Params
        req.setNotifyUrl(" https://7504-2407-c00-6003-dd4e-8406-c0f6-1553-33bb.ngrok-free.app/Server_war_exploded/NotifyPayment");           // Notifiy Url
//        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
//        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
//        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");

        if(items!=null){
            for (Item item: items) {
                req.getItems().add(item);
            }
        }

//        req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));

        Intent intent = new Intent(activity, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
//        startActivityForResult(intent, "22620"); //unique request ID e.g. "11001"
        startActivityForResult(activity,intent, requestId, null); //unique request ID e.g. "11001"

    }

}
