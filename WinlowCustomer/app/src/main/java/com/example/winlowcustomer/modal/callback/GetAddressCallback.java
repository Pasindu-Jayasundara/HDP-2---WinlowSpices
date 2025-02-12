package com.example.winlowcustomer.modal.callback;

import java.util.ArrayList;
import java.util.List;

public abstract class GetAddressCallback {

    public void onAddressLoaded(List<String> addressListNew){}
    public void onAddressAdded(ArrayList<String> addressList){}
    public void onAddressFileExists(boolean exists){}
    public void onAddressFileCreated(boolean created){};
    public void onAddressDeleted(boolean deleted){};
}
