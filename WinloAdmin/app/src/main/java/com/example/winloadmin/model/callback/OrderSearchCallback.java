package com.example.winloadmin.model.callback;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface OrderSearchCallback {

    void onOrderSearch(boolean isSuccess, QuerySnapshot documentSnapshots);
}
