package com.example.winloadmin.model.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface OrderLoadCallback {

    void onOrderLoad(boolean isSuccess, List<DocumentSnapshot> documentSnapshots);
}
