package com.example.winlowcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

                if(!addressList.isEmpty()){
                    addressList.remove(0);
                    AddressLoadingRecyclerViewAdapter addressRecyclerViewAdapter = new AddressLoadingRecyclerViewAdapter(addressList,getApplicationContext());
                    addressRecyclerView.setAdapter(addressRecyclerViewAdapter);
                }


            }
        });

        // add new address
        Button btn = findViewById(R.id.button9);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddressActivity.this, AddNewAddressActivity.class);
                startActivity(intent);

            }
        });

        // go back
        ImageButton backBtn = findViewById(R.id.imageButton15);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOnBackPressedDispatcher().onBackPressed();

            }
        });
    }
}