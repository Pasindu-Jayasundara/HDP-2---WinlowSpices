package com.example.winloadmin.model;

import static com.example.winloadmin.nav.BannerFragment.bannerDTOList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.BannerDTO;
import com.example.winloadmin.model.callback.BannerDeleteCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BannerRecyclerViewAdapter extends RecyclerView.Adapter<BannerRecyclerViewAdapter.BannerRecyclerViewHolder>{


    @NonNull
    @Override
    public BannerRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.banner_card, parent, false);

        return new BannerRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerRecyclerViewHolder holder, int position) {

        BannerDTO bannerDTO = bannerDTOList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(bannerDTO.getPath())
                .placeholder(R.drawable.product_image)
                .error(R.drawable.product_image)
                .into(holder.bannerImg);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeFromDB(bannerDTO, new BannerDeleteCallback() {
                    @Override
                    public void onDelete(boolean isDeleted) {

                        if(isDeleted){

                            bannerDTOList.remove(position);
                            notifyItemRemoved(position);

                        }else{
                            Toast.makeText(v.getContext(), R.string.banner_delete_failed, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


    }

    private void removeFromDB(BannerDTO bannerDTO, BannerDeleteCallback bannerDeleteCallback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banner")
                .where(Filter.equalTo("path",bannerDTO.getPath()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        db.collection("banner").document(queryDocumentSnapshots.getDocuments().get(0).getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        bannerDeleteCallback.onDelete(true);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        bannerDeleteCallback.onDelete(false);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        bannerDeleteCallback.onDelete(false);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return bannerDTOList.size();
    }

    public class BannerRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView bannerImg;
        ImageButton deleteBtn;

        public BannerRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerImg = itemView.findViewById(R.id.imageView10);
            deleteBtn = itemView.findViewById(R.id.imageButton4);
        }
    }

}
