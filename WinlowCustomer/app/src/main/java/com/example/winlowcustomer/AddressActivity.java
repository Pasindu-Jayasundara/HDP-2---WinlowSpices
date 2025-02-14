package com.example.winlowcustomer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.modal.AddressHandling;
import com.example.winlowcustomer.modal.AddressLoadingRecyclerViewAdapter;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 3;

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

//        SetUpLanguage.setAppLanguage(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView5, BottomNavigationFragment.class,null)
                .setReorderingAllowed(true)
                .commit();


        // Check and request storage permissions
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { // Android 13+
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//            } else {
//                loadAddresses();
//            }
//        } else {
//            loadAddresses(); // No need for permissions in Android 13+
//        }

        // Add new address button click listener
        Button btn = findViewById(R.id.button9);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        // Go back button
        ImageButton backBtn = findViewById(R.id.imageButton15);
        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Load addresses if permissions are granted
    private void loadAddresses() {

//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//        }

        RecyclerView addressRecyclerView = findViewById(R.id.addressRecyclerView);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AddressHandling.loadAddress(getApplicationContext(), new GetAddressCallback() {
            @Override
            public void onAddressLoaded(List<String> addressListNew) {
                Log.i("address", addressListNew.toString());
                if (!addressListNew.isEmpty()) {

                    if(addressListNew.contains(getString(R.string.checkout_select_address))){
                        List<String> list = new ArrayList<>();
                        list.add(getString(R.string.checkout_select_address));
                        list.add(getString(R.string.select_address));
                        addressListNew.removeAll(list);
                    }
                    AddressLoadingRecyclerViewAdapter adapter = new AddressLoadingRecyclerViewAdapter(addressListNew, getApplicationContext());
                    addressRecyclerView.setAdapter(adapter);
                }
            }
        });
//        AddressHandling.loadAddress(getApplicationContext(), addressList -> {
//
//        });
    }

    // Request Storage Permission when adding an address
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                startActivity(new Intent(AddressActivity.this, AddNewAddressActivity.class));
            }
        } else {
            startActivity(new Intent(AddressActivity.this, AddNewAddressActivity.class)); // No need for permission
        }
    }

    // Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                loadAddresses();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAddresses();

    }
}