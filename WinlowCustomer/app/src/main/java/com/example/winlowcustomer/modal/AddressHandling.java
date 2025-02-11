package com.example.winlowcustomer.modal;

//import static android.provider.Settings.System.getString;

import static android.provider.Settings.System.getString;

import android.content.Context;

import com.example.winlowcustomer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AddressHandling {

    private static final String FILE_PATH = "data/data/com.example.winlowcustomer/files/address.txt";

    public static List<String> loadAddress(Context context) {

        List<String> addressList = new ArrayList<>();
        addressList.add(context.getString(R.string.checkout_select_address));

        if(doesAddressFileExists()){

            File file= new File(FILE_PATH);
            if(file.canRead()){

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

                    String address;
                    while((address = bufferedReader.readLine()) != null){
                        addressList.add(address);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        }

        return addressList;

    }

    private static boolean doesAddressFileExists(){

        File file  = new File(FILE_PATH);
        return file.exists();

    }
    private static boolean createAddressFile() {
        File file = new File(FILE_PATH);
        try {

            if(!file.exists()){
                file.createNewFile();
                file.setReadable(true);
                file.setWritable(true);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
