package com.example.winlowcustomer;

import android.app.ComponentCaller;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        SetUpLanguage.setAppLanguage(getApplicationContext());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView4, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        if(userJson!=null){

            UserDTO userDTO = new Gson().fromJson(userJson, UserDTO.class);

            TextView textView14 = findViewById(R.id.textView14);
            textView14.setText(userDTO.getName());

            if(userDTO.getProfile_image()!=null){
                ImageView img = findViewById(R.id.imageView4);
                Glide.with(this)
                        .load(userDTO.getProfile_image())
                        .placeholder(R.drawable.empty_profile_2)
                        .error(R.drawable.empty_profile_2)
                        .into(img);
            }

        }


        // profile address card view
        CardView profileAddressCardView = findViewById(R.id.cardView3);
        profileAddressCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(AddressActivity.class);
            }
        });

        // profile address img
        Button profileAddressImg = findViewById(R.id.imageButton8);
        profileAddressImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(AddressActivity.class);
            }
        });

        // profile address arrow
        Button profileAddressArrow = findViewById(R.id.imageButton9);
        profileAddressCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(AddressActivity.class);
            }
        });

        // profile language card view
        CardView profileLanguageCardView = findViewById(R.id.cardView5);
        profileLanguageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LanguageActivity.class);
            }
        });

        // profile language img
        Button profileLanguageImg = findViewById(R.id.imageButton10);
        profileLanguageImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LanguageActivity.class);
            }
        });

        // profile language arrow
        Button profileLanguageArrow = findViewById(R.id.imageButton11);
        profileLanguageArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LanguageActivity.class);
            }
        });

        // img
        ImageView profileImage = findViewById(R.id.imageView4);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imageUri = data.getData();

            ImageView profileImage = findViewById(R.id.imageView4);
            profileImage.setImageURI(imageUri); // Set the selected image to ImageView

            imageUpload(imageUri);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1000);
    }

    private void imageUpload(Uri imageUri) {

        if (imageUri != null) {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference firebaseStorageReference = firebaseStorage.getReference();

            StorageReference fileRef = firebaseStorageReference.child("profileImage/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
                                            String userJson = sharedPreferences.getString("user", null);

                                            if(userJson!=null){

                                                Gson gson = new Gson();
                                                UserDTO userDTO = gson.fromJson(userJson, UserDTO.class);

                                                Map<String, Object> imgMap = new HashMap<>();
                                                imgMap.put("profile_image", uri.toString());

                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("user").document(userDTO.getId())
                                                        .set(imgMap, SetOptions.merge())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                                Toast.makeText(AccountActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

                            ImageView profileImage = findViewById(R.id.imageView4);
                            profileImage.setImageResource(R.drawable.empty_profile_2);
                        }
                    });
        }

    }

    private void gotoActivity(Class<?> activity) {

        Intent intent = new Intent(AccountActivity.this, activity);
        startActivity(intent);
    }
}