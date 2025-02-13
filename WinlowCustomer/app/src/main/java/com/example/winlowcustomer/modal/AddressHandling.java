package com.example.winlowcustomer.modal;

import android.content.Context;
import android.widget.Toast;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddressHandling {

    private static String getFilePath(Context context) {
        return new File(context.getFilesDir(), "address.txt").getAbsolutePath();
    }

    public static void loadAddress(Context context, GetAddressCallback getAddressCallback) {
        List<String> addressList = new ArrayList<>();
        addressList.add(context.getString(R.string.checkout_select_address));

        File file = new File(getFilePath(context));

        new Thread(() -> {
            if (!file.exists() || !file.canRead()) {
                getAddressCallback.onAddressLoaded(addressList);
                return;
            }

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String address;
                while ((address = bufferedReader.readLine()) != null) {
                    addressList.add(address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getAddressCallback.onAddressLoaded(addressList);
        }).start();
    }

    public static void saveAddress(String typeText, Context context) {
        File file = new File(getFilePath(context));

        new Thread(() -> {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                List<String> addressList = new ArrayList<>();
                loadAddress(context, new GetAddressCallback() {
                    @Override
                    public void onAddressLoaded(List<String> addressListNew) {
                        if(addressListNew.contains(context.getString(R.string.checkout_select_address))){
                            List<String> list = new ArrayList<>();
                            list.add(context.getString(R.string.checkout_select_address));
                            list.add(context.getString(R.string.select_address));
                            addressListNew.removeAll(list);
                        }
                        addressList.clear();
                        addressList.add(typeText);
                        addressList.addAll(addressListNew);

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                            for (String address : addressList) {
                                writer.write(address);
                                writer.newLine();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.file_create_failed, Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public static void deleteAddress(String addressToRemove, Context context, GetAddressCallback getAddressCallback) {
        File file = new File(getFilePath(context));

        new Thread(() -> {
            if (!file.exists() || !file.canRead()) {
                getAddressCallback.onAddressLoaded(new ArrayList<>());
                return;
            }

            List<String> updatedAddresses = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(addressToRemove)) {
                        updatedAddresses.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                for (String address : updatedAddresses) {
                    writer.write(address);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            getAddressCallback.onAddressLoaded(updatedAddresses);
        }).start();
    }
}
