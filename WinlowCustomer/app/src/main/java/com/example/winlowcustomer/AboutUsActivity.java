package com.example.winlowcustomer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.GeoPointDTO;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AboutUsActivity extends AppCompatActivity {

    List<GeoPointDTO> geoPointDTOList = new ArrayList<>();
    boolean isMapLoaded;
    boolean isDataLoaded;
    GoogleMap googleMap2;
    public static final String webUrl = "https://winloflavors.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView13, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        // back
        ImageButton back = findViewById(R.id.imageButton26);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // get location
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shop").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot document : documentSnapshotList){

                                List<GeoPoint> geoPointList = (List<GeoPoint>) document.get("location_array");
                                if(geoPointList!=null){
                                    if(!geoPointList.isEmpty()){

                                        for(GeoPoint geoPoint:geoPointList){

                                            GeoPointDTO geoPointDTO = new GeoPointDTO();
                                            geoPointDTO.setLatitude(geoPoint.getLatitude());
                                            geoPointDTO.setLongitude(geoPoint.getLongitude());

                                            geoPointDTOList.add(geoPointDTO);

                                        }


                                        loadMap();

                                    }
                                }

                            }

                        }

                        isDataLoaded = true;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isDataLoaded = true;
                    }
                });

        // map
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView14, supportMapFragment)
                .setReorderingAllowed(true)
                .commit();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                googleMap2 = googleMap;
                isMapLoaded = true;

                loadMap();

            }
        });

        // website
        CardView cardView = findViewById(R.id.cardView50);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet();
            }
        });
        Button imgBtn = findViewById(R.id.imageButton55);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet();
            }
        });

    }

    private void bottomSheet() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);

        View inflated = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_layout, null);
        WebView webView = inflated.findViewById(R.id.web);
        webView.loadUrl(webUrl);
        webView.getSettings().setJavaScriptEnabled(true);

        bottomSheetDialog.setContentView(inflated);
        bottomSheetDialog.show();

    }

    private void loadMap() {

        if(isMapLoaded && isDataLoaded){

            for(GeoPointDTO geoPointDTO:geoPointDTOList){

                LatLng latLng = new LatLng(geoPointDTO.getLatitude(), geoPointDTO.getLongitude());

                googleMap2.addMarker(
                        new MarkerOptions().position(latLng).title(getString(R.string.title_about_us_shop)).icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                ).showInfoWindow();

            }

        }

    }
}