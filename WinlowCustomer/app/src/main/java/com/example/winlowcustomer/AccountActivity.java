package com.example.winlowcustomer;

import static com.example.winlowcustomer.MainActivity.language;
import static com.example.winlowcustomer.MainActivity.sqliteVersion;

import android.app.ComponentCaller;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.winlowcustomer.modal.AddressHandling;
import com.example.winlowcustomer.modal.SQLiteHelper;
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

        ImageButton back = findViewById(R.id.imageButton5);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
//        String language = sharedPreferences.getString("language", "");
//        SetUpLanguage.setAppLanguage(AccountActivity.this, language);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView4, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        String userJson = sharedPreferences.getString("user", null);
        Log.i("vta","3 :"+userJson);

        ImageView img = findViewById(R.id.imageView4);

        if(userJson!=null){
            Log.i("vta","2 :"+userJson);

            UserDTO userDTO = new Gson().fromJson(userJson, UserDTO.class);
            Log.i("vta","4 :"+new Gson().toJson(userDTO));

            TextView textView14 = findViewById(R.id.textView19);
            textView14.setText(userDTO.getName());

            if(userDTO.getProfile_image()!=null){
                Glide.with(this)
                        .load(userDTO.getProfile_image())
                        .circleCrop()
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
        profileAddressArrow.setOnClickListener(new View.OnClickListener() {
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
//        ImageView profileImage = findViewById(R.id.imageView4);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // logout
        CardView logoutView = findViewById(R.id.cardView50);
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        Button logoutViewIcon = findViewById(R.id.imageButton51);
        logoutViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        Button logoutViewIcon2 = findViewById(R.id.imageButton52);
        logoutViewIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    private void logout() {

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String user = sharedPreferences.getString("user", null);
        if(user!=null){

            UserDTO userDTO = new Gson().fromJson(user, UserDTO.class);
            SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext(), "winlow.db", null, sqliteVersion);
            sqLiteHelper.removeUser(sqLiteHelper,userDTO.getId());
        }

        AddressHandling.clearAllAddress(getApplicationContext());

        String language = sharedPreferences.getString("language", "en");

        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putString("language",language).apply();
        gotoActivity(MainActivity.class);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("vta","1 :"+new Gson().toJson(data));

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Log.i("vta",new Gson().toJson(data));

            Uri imageUri = data.getData();

            ImageView profileImage = findViewById(R.id.imageView4);
            profileImage.setImageURI(imageUri);

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
        if (imageUri == null) return;

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        if (userJson == null) return;

        Gson gson = new Gson();
        UserDTO userDTO = gson.fromJson(userJson, UserDTO.class);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the current image URL from Firestore
        db.collection("user").document(userDTO.getId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String existingImageUrl = documentSnapshot.getString("profile_image");

                        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                            // Extract the file name from the URL
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference existingFileRef = firebaseStorage.getReferenceFromUrl(existingImageUrl);

                            // Delete the existing file
                            existingFileRef.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Proceed with uploading the new image after successful deletion
                                        uploadNewImage(imageUri, userDTO, sharedPreferences, gson);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure in deleting the existing image
                                        Toast.makeText(AccountActivity.this, "Failed to delete old image", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // No previous image, proceed with upload
                            uploadNewImage(imageUri, userDTO, sharedPreferences, gson);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadNewImage(Uri imageUri, UserDTO userDTO, SharedPreferences sharedPreferences, Gson gson) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference firebaseStorageReference = firebaseStorage.getReference();

        StorageReference fileRef = firebaseStorageReference.child("profileImage/" + System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Update Firestore with new image URL
                            Map<String, Object> imgMap = new HashMap<>();
                            imgMap.put("profile_image", uri.toString());

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("user").document(userDTO.getId())
                                    .set(imgMap, SetOptions.merge())
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(AccountActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();

                                        // Update local user data
                                        userDTO.setProfile_image(uri.toString());
                                        sharedPreferences.edit().putString("user", gson.toJson(userDTO)).apply();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();

                    ImageView profileImage = findViewById(R.id.imageView4);
                    profileImage.setImageResource(R.drawable.empty_profile_2);
                });
    }


    private void gotoActivity(Class<?> activity) {

        Intent intent = new Intent(AccountActivity.this, activity);
        startActivity(intent);
    }
}