package com.example.winlowcustomer.modal;

//import static android.provider.Settings.System.getString;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.widget.Toast;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AddressHandling {

    private static final String FILE_PATH = "data/data/com.example.winlowcustomer/files/address.txt";

    public static void loadAddress(Context context, GetAddressCallback getAddressCallback) {

        List<String> addressList = new ArrayList<>();
        addressList.add(context.getString(R.string.checkout_select_address));

        doesAddressFileExists(new GetAddressCallback() {
            @Override
            public void onAddressFileExists(boolean exists) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = new File(FILE_PATH);
                        if (file.canRead()) {

                            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

                                String address;
                                while ((address = bufferedReader.readLine()) != null) {
                                    addressList.add(address);
                                }

                                getAddressCallback.onAddressLoaded(addressList);

                            } catch (Exception e) {
                                e.printStackTrace();

                                getAddressCallback.onAddressLoaded(addressList);

                            }

                        }else{
                            getAddressCallback.onAddressLoaded(addressList);
                        }

                    }
                }).start();

            }
        });

    }

    private static void doesAddressFileExists(GetAddressCallback getAddressCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(FILE_PATH);

                getAddressCallback.onAddressFileExists(file.exists());

            }
        }).start();

    }

    private static void createAddressFile(GetAddressCallback getAddressCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(FILE_PATH);
                try {

                    if (!file.exists()) {
                        file.createNewFile();
                        file.setReadable(true);
                        file.setWritable(true);

                        getAddressCallback.onAddressFileCreated(true);
                    }else{
                        getAddressCallback.onAddressFileCreated(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    getAddressCallback.onAddressFileCreated(false);

                }

            }
        }).start();

    }

    public static void saveAddress(String typeText, Context context) {

        doesAddressFileExists(new GetAddressCallback() {

            @Override
            public void onAddressFileExists(boolean exists) {

                createAddressFile(new GetAddressCallback() {
                    @Override
                    public void onAddressFileCreated(boolean created) {

                        if(!created){
                            Toast.makeText(context, R.string.file_create_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                File file = new File(FILE_PATH);
                                try {

                                    if (!file.canWrite()) {
                                        return;
                                    }

                                    final boolean[] overwrite = {true};

                                    FileWriter fileWriter = new FileWriter(file);
                                    final BufferedWriter[] bufferedWriter = {null};

                                    loadAddress(context, new GetAddressCallback() {
                                        @Override
                                        public void onAddressLoaded(List<String> addressList) {

                                            addressList.add(typeText);

                                            try{

                                                for (String loadAddress : addressList) {

                                                    if (bufferedWriter[0] == null) {
                                                        bufferedWriter[0] = new BufferedWriter(new java.io.FileWriter(file, overwrite[0]));
                                                        overwrite[0] = false;
                                                    } else {
                                                        bufferedWriter[0] = new BufferedWriter(new java.io.FileWriter(file, overwrite[0]));
                                                    }

                                                    bufferedWriter[0].write(loadAddress);
                                                    bufferedWriter[0].newLine();

                                                }

                                                bufferedWriter[0].close();
                                                fileWriter.close();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                    }
                });

            }

        });

    }

    public static void deleteAddress(String address, Context context, GetAddressCallback getAddressCallback) {

    }
}
