package com.example.winlowcustomer.modal.callback;

import org.bouncycastle.its.asn1.Latitude;
import org.bouncycastle.its.asn1.Longitude;

public interface GetCoordinationCallback {

    void onCoordinationReceived(double latitude, double longitude, String typedAddress);
}
