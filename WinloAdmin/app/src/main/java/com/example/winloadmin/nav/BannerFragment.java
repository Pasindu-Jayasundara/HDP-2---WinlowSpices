package com.example.winloadmin.nav;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.example.winloadmin.model.callback.BannerDBUpdateCallback;
import com.example.winloadmin.model.callback.BannerUploadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.List;

public class BannerFragment extends Fragment {

    String imageUrl;
    public static List<BannerDTO> bannerDTOList;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button addNewBtn = view.findViewById(R.id.button19);
        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View inflated = layoutInflater.inflate(R.layout.new_banner, v.findViewById(R.id.recyclerView3), false);

                ImageView banner = inflated.findViewById(R.id.imageView11);
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

                        if(imageUrl == null){
                            Toast.makeText(v.getContext(), R.string.select_banner, Toast.LENGTH_SHORT).show();
                        }else{
                            uploadImage(new BannerUploadCallback() {
                                @Override
                                public void onBannerUpload(boolean isSuccess, String newImageUrl) {

                                    if(isSuccess){

                                        BannerDTO bannerDTO = new BannerDTO();
                                        bannerDTO.setPath(imageUrl);
                                        bannerDTOList.add(0,bannerDTO);

                                        addToDB(bannerDTO,new BannerDBUpdateCallback(){
                                            @Override
                                            public void onBannerDBUpdate(boolean isSuccess) {

                                                if(isSuccess){

                                                    RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView3);
                                                    recyclerView.getAdapter().notifyItemInserted(0);

                                                    Toast.makeText(v.getContext(), R.string.banner_upload_success, Toast.LENGTH_SHORT).show();

                                                }else{
                                                    deleteBanner();
                                                    Toast.makeText(v.getContext(), R.string.banner_upload_failed, Toast.LENGTH_SHORT).show();
                                                }

                                                alertDialog.dismiss();

                                            }
                                        });

                                    }else{
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

    private void deleteBanner() {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference existingFileRef = firebaseStorage.getReferenceFromUrl(imageUrl);

        // Delete the existing file
        existingFileRef.delete();

    }

    private void addToDB(BannerDTO bannerDTO, BannerDBUpdateCallback bannerDBUpdateCallback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banner")
                .add(bannerDTO)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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

                            bannerUploadCallback.onBannerUpload(true,uri.toString());

                        })
                        .addOnFailureListener(e -> {
                            bannerUploadCallback.onBannerUpload(false,null);
                        })
                )
                .addOnFailureListener(e -> {
                    bannerUploadCallback.onBannerUpload(false,null);
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

            ImageView imageView = getActivity().findViewById(R.id.imageView11);
            imageView.setImageURI(imageUri);

        }

    }

}