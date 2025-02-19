package com.example.winloadmin.nav;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.BannerDTO;
import com.example.winloadmin.model.BannerRecyclerViewAdapter;
import com.example.winloadmin.model.callback.BannerDBUpdateCallback;
import com.example.winloadmin.model.callback.BannerLoadCallback;
import com.example.winloadmin.model.callback.BannerUploadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerFragment extends Fragment {

    String imageUrl;
    public static List<BannerDTO> bannerDTOList;
    AlertDialog alertDialog;
    ImageView banner;
    String docId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadBanners(new BannerLoadCallback() {
            @Override
            public void onBannerLoad(boolean isSuccess, List<BannerDTO> bannerDTOList2) {
                if (isSuccess) {

                    if(bannerDTOList2.isEmpty()){
                        bannerDTOList = new ArrayList<>();
                    }else{
                        bannerDTOList = bannerDTOList2;
                        BannerRecyclerViewAdapter bannerRecyclerViewAdapter = new BannerRecyclerViewAdapter();
                        recyclerView.setAdapter(bannerRecyclerViewAdapter);
                    }

                }
            }
        });

        Button addNewBtn = view.findViewById(R.id.button19);
        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View inflated = layoutInflater.inflate(R.layout.new_banner, v.findViewById(R.id.recyclerView3), false);

                banner = inflated.findViewById(R.id.imageView11);
                banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectImage();

                    }
                });

                Button btn = inflated.findViewById(R.id.button20);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imageUrl == null) {
                            Toast.makeText(v.getContext(), R.string.select_banner, Toast.LENGTH_SHORT).show();
                        } else {

                            btn.setText(R.string.saving);

                            uploadImage(new BannerUploadCallback() {
                                @Override
                                public void onBannerUpload(boolean isSuccess, String newImageUrl) {

                                    if (isSuccess) {

                                        BannerDTO bannerDTO = new BannerDTO();
                                        bannerDTO.setPath(newImageUrl);
                                        bannerDTO.setId(docId);
                                        bannerDTOList.add(0, bannerDTO);

                                        addToDB(bannerDTO, new BannerDBUpdateCallback() {
                                            @Override
                                            public void onBannerDBUpdate(boolean isSuccess) {

                                                if (isSuccess) {

                                                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView3);
                                                    if(recyclerView.getAdapter()!=null){
                                                        recyclerView.getAdapter().notifyItemInserted(0);
                                                    }else{
                                                        BannerRecyclerViewAdapter bannerRecyclerViewAdapter = new BannerRecyclerViewAdapter();
                                                        recyclerView.setAdapter(bannerRecyclerViewAdapter);
                                                    }

                                                    Toast.makeText(v.getContext(), R.string.banner_upload_success, Toast.LENGTH_SHORT).show();

                                                } else {
                                                    deleteBanner();
                                                    Toast.makeText(v.getContext(), R.string.banner_upload_failed, Toast.LENGTH_SHORT).show();
                                                }

                                                alertDialog.dismiss();

                                            }
                                        });

                                    } else {
                                        Toast.makeText(v.getContext(), R.string.banner_upload_failed, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }

                    }
                });

                alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(inflated).show();

            }
        });

    }

    private void loadBanners(BannerLoadCallback bannerLoadCallback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banner").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<BannerDTO> bannerDTOs = new ArrayList<>();
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {

                            docId = documentSnapshot.getId();

                            List<String> pathList = (List<String>) documentSnapshot.get("path");
                            for (String path : pathList) {
                                BannerDTO bannerDTO = new BannerDTO(documentSnapshot.getId(), path);
                                bannerDTO.setPath(path);

                                bannerDTOs.add(bannerDTO);

                            }

                        }

                        bannerLoadCallback.onBannerLoad(true, bannerDTOs);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bannerLoadCallback.onBannerLoad(false, null);
                    }
                });

    }

    private void deleteBanner() {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference existingFileRef = firebaseStorage.getReferenceFromUrl(imageUrl);

        // Delete the existing file
        existingFileRef.delete();

    }

    private void addToDB(BannerDTO bannerDTO, BannerDBUpdateCallback bannerDBUpdateCallback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banner")
                .document(bannerDTO.getId())
                .update("path", FieldValue.arrayUnion(bannerDTO.getPath()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        bannerDBUpdateCallback.onBannerDBUpdate(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bannerDBUpdateCallback.onBannerDBUpdate(false);
                    }
                });

    }

    private void uploadImage(BannerUploadCallback bannerUploadCallback) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference firebaseStorageReference = firebaseStorage.getReference();

        StorageReference fileRef = firebaseStorageReference.child("bannerImage/" + System.currentTimeMillis() + ".jpg");
        fileRef.putFile(Uri.parse(imageUrl))
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            bannerUploadCallback.onBannerUpload(true, uri.toString());

                        })
                        .addOnFailureListener(e -> {
                            bannerUploadCallback.onBannerUpload(false, null);
                        })
                )
                .addOnFailureListener(e -> {
                    bannerUploadCallback.onBannerUpload(false, null);
                });

    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2000 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Log.i("vta", new Gson().toJson(data));

            Uri imageUri = data.getData();

            imageUrl = imageUri.toString();

            banner.setImageURI(imageUri);

        }

    }

}