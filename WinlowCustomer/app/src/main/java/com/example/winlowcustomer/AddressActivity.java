package com.example.winlowcustomer;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.modal.AddressHandling;
import com.example.winlowcustomer.modal.AddressLoadingRecyclerViewAdapter;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;

import java.util.List;

public class AddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // load address

        RecyclerView addressRecyclerView = findViewById(R.id.addressRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        addressRecyclerView.setLayoutManager(linearLayoutManager);

        AddressHandling.loadAddress(getApplicationContext(), new GetAddressCallback() {
            @Override
            public void onAddressLoaded(List<String> addressList) {

                AddressLoadingRecyclerViewAdapter addressRecyclerViewAdapter = new AddressLoadingRecyclerViewAdapter(addressList,getApplicationContext());
                addressRecyclerView.setAdapter(addressRecyclerViewAdapter);

            }
        });

    }
}