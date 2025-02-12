package com.example.winlowcustomer.modal.callback;

import java.util.ArrayList;
import java.util.List;

public abstract class GetAddressCallback {

    public void onAddressLoaded(List<String> addressList){}
    public void onAddressAdded(ArrayList<String> addressList){}
    public void onAddressFileExists(boolean exists){}
    public void onAddressFileCreated(boolean created){};
}
