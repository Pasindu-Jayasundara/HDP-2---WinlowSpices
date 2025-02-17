package com.example.winloadmin.model.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface CustomerLoadCallback {

    void onCustomerLoad(boolean isSuccess, List<DocumentSnapshot> documentSnapshots);
}
