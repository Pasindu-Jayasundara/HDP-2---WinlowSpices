package com.example.winlowcustomer;

import static com.example.winlowcustomer.MainActivity.language;

import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.callback.SaveAddressCallback;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.winlowcustomer.modal.AddressHandling;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;
import com.example.winlowcustomer.modal.callback.GetCoordinationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bouncycastle.its.asn1.Latitude;
import org.bouncycastle.its.asn1.Longitude;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddNewAddressActivity extends AppCompatActivity {

    List<String> addressList = new ArrayList<>();
    boolean isFromMap;
    Marker marker;
    GoogleMap loadedGoogleMap;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        SetUpLanguage.setAppLanguage(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageButton back = findViewById(R.id.imageButton14);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // enter address
        // select address on map

        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout4);
        Spinner spinner = findViewById(R.id.spinner);

        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        frameLayout.bringChildToFront(textInputLayout);
        spinner.setVisibility(View.INVISIBLE);

        RadioGroup radioGroup = findViewById(R.id.radioGroup2);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioButton3) { // type
                    textInputLayout.setVisibility(View.VISIBLE);
                    frameLayout.bringChildToFront(textInputLayout);
                    spinner.setVisibility(View.INVISIBLE);
                    isFromMap = false;
                } else if (checkedId == R.id.radioButton4) { // select
                    frameLayout.bringChildToFront(spinner);
                    spinner.setVisibility(View.VISIBLE);
                    textInputLayout.setVisibility(View.INVISIBLE);
                    isFromMap = true;
                }
            }
        });

        // select address
        addressList.add(getString(R.string.select));

        ArrayAdapter arrayAdapter = new ArrayAdapter(
                AddNewAddressActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                addressList
        );
        spinner.setAdapter(arrayAdapter);

        // type address
        TextInputEditText typeAddress = findViewById(R.id.typeAddress);
        typeAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    String typeText = typeAddress.getText().toString();
                    loadLocationFromAddress(typeText, new GetCoordinationCallback() {
                        @Override
                        public void onCoordinationReceived(double latitude, double longitude,String typedAddress) {

                            if (marker != null) {
                                marker.remove();
                            }

                            LatLng latLng = new LatLng(latitude, longitude);

                            Drawable drawable = ContextCompat.getDrawable(AddNewAddressActivity.this, R.drawable.location_new);
                            Bitmap bitmap = drawableToBitmap(drawable);

                            MarkerOptions markerOprion = new MarkerOptions()
                                    .position(latLng)
                                    .title(getString(R.string.title)+": "+typedAddress)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                            marker = loadedGoogleMap.addMarker(markerOprion);
                            marker.showInfoWindow();
                            loadedGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition.Builder().target(latLng).zoom(15).build()
                            ));

                        }
                    });

                    return true;
                }

                return false;
            }
        });

        // map
        SupportMapFragment supportMapFragment = new SupportMapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mapFrameLayout, supportMapFragment).commit();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    googleMap.setMyLocationEnabled(true);
                    myLocation(googleMap);
                }else{
                    String[] permissionArray = {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(AddNewAddressActivity.this, permissionArray,1);
                }

                LatLng latLng = new LatLng(6.930053224303333, 79.84787284365264);
                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder().target(latLng).zoom(15).build()
                        )
                );

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                myLocation(googleMap);


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                        if (isFromMap) {

                            if (marker != null) {
                                marker.remove();
                            }

                            Drawable drawable = ContextCompat.getDrawable(AddNewAddressActivity.this, R.drawable.location_new);
                            Bitmap bitmap = drawableToBitmap(drawable);

                            MarkerOptions markerOprion = new MarkerOptions()
                                    .position(latLng)
                                    .title(getString(R.string.title))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                            marker = googleMap.addMarker(markerOprion);
                            marker.showInfoWindow();

                            loadLocationFromCoordination(latLng, new GetAddressCallback() {
                                @Override
                                public void onAddressLoaded(List<String> addressListNew) {

                                    addressList.clear();
                                    addressList.add(getString(R.string.select));
                                    addressList.addAll(addressListNew);
                                    arrayAdapter.notifyDataSetChanged();

                                }
                            });
                        }

                    }
                });
                loadedGoogleMap = googleMap;

            }
        });

        // save address
        Button saveButton = findViewById(R.id.button10);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFromMap) {

                   if(spinner.getSelectedItemId()==0){
                       Toast.makeText(AddNewAddressActivity.this, R.string.select_address, Toast.LENGTH_SHORT).show();
                       return;
                   }

                    String typeText = addressList.get(spinner.getSelectedItemPosition()-1);
                    AddressHandling.saveAddress(typeText, getApplicationContext(), new SaveAddressCallback() {
                        @Override
                        public void onAddressSave(boolean isSuccess) {

                                Intent receivedIntent = getIntent();
                                if(receivedIntent.hasExtra("to")){

                                    String from = receivedIntent.getStringExtra("to");
                                    if(from!=null && from.equals("checkout")){
                                        Intent intent = new Intent(AddNewAddressActivity.this, CheckoutActivity.class);

                                        Log.i("cccccccccc","111111111");

                                        intent.putExtra("cameFrom","addNewAddress");
                                        intent.putExtra("paymentData",receivedIntent.getStringExtra("paymentData"));
                                        intent.putExtra("userDto",receivedIntent.getStringExtra("userDto"));
                                        intent.putExtra("totalPrice",receivedIntent.getStringExtra("totalPrice"));

                                        startActivity(intent);
                                    }

                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getOnBackPressedDispatcher().onBackPressed();
                                        }
                                    });
                                }

                        }
                    });

                }else{

                    String typeText = typeAddress.getText().toString();
                    AddressHandling.saveAddress(typeText, getApplicationContext(), new SaveAddressCallback() {
                        @Override
                        public void onAddressSave(boolean isSuccess) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getOnBackPressedDispatcher().onBackPressed();
                                }
                            });
                        }
                    });

                }

            }
        });

    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @SuppressLint("MissingPermission")
    private void myLocation(GoogleMap googleMap) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(AddNewAddressActivity.this);
        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(latLng).zoom(18).build()
                    ));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                loadedGoogleMap.setMyLocationEnabled(true);
                myLocation(loadedGoogleMap);
            }
        }
    }

    private void loadLocationFromAddress(String typeText, GetCoordinationCallback getCoordinationCallback) {

            try {
                String encodedAddress = URLEncoder.encode(typeText, "UTF-8");
                String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=AIzaSyBcrR9pIzvt62aNLuposODjNdOPclrZpsw";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).build();

                        try {
                            Response response = client.newCall(request).execute();
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);

                            JsonArray results = jsonObject.getAsJsonArray("results");
                            if (results != null && results.size() > 0) {

                                JsonObject geometry = results.get(0).getAsJsonObject().getAsJsonObject("geometry");
                                JsonObject location = geometry.getAsJsonObject("location");

                                double latitude = location.get("lat").getAsDouble();
                                double longitude = location.get("lng").getAsDouble();

                                // Send the result back to the UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getCoordinationCallback.onCoordinationReceived(latitude, longitude,typeText);
                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    private void loadLocationFromCoordination(LatLng latLng, GetAddressCallback getAddressCallback) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=AIzaSyBcrR9pIzvt62aNLuposODjNdOPclrZpsw";

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.body() == null) {
                        return;
                    }

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);

                    ArrayList<String> formattedAddresses = new ArrayList<>();

                    JsonArray resultsArray = jsonObject.getAsJsonArray("results");
                    if (resultsArray != null) {
                        for (int i = 0; i < resultsArray.size(); i++) {
                            JsonObject result = resultsArray.get(i).getAsJsonObject();
                            String formattedAddress = result.get("formatted_address").getAsString();
                            formattedAddresses.add(formattedAddress);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAddressCallback.onAddressLoaded(formattedAddresses);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}