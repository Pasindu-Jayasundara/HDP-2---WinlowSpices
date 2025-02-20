package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.MainActivity.language;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winlowcustomer.ProductViewActivity;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.modal.callback.TranslationCallback;
import com.google.gson.Gson;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ProductViewHolder> {

    ArrayList<ProductDTO> productDTOArrayList;
    Activity activity;

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        FrameLayout discountBadge;
        TextView discountText;
        TextView productName;
        TextView productPrice;
        ConstraintLayout productCardConstraintLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.imageView12);
            discountBadge = itemView.findViewById(R.id.discountBadgeFrameLayout);
            discountText = itemView.findViewById(R.id.textView58);
            productName = itemView.findViewById(R.id.textView53);
            productPrice = itemView.findViewById(R.id.textView54);
            productCardConstraintLayout = itemView.findViewById(R.id.productCardContraintLayout);
        }
    }

    public HomeRecyclerViewAdapter(ArrayList<ProductDTO> productDTOArrayList, Activity activity) {
        this.productDTOArrayList = productDTOArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HomeRecyclerViewAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View productCard = layoutInflater.inflate(R.layout.product_card_layout, parent, false);

        return new HomeRecyclerViewAdapter.ProductViewHolder(productCard);

    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.ProductViewHolder holder, int position) {

        ProductDTO productDTO = productDTOArrayList.get(position);

        String imageUrl = productDTO.getImage_path();
        Glide.with(holder.itemView.getContext()) // Use context
                .load(imageUrl) // Load image URL
                .placeholder(R.drawable.product_placeholder) // Optional: placeholder image
                .error(R.drawable.product_placeholder) // Optional: error image
                .into(holder.productImage); // Set image into ImageView

        String name = productDTO.getName();
        if (!language.equals("en")) {
            Translate.translateText(productDTO.getName(), language, new TranslationCallback() {
                @Override
                public void onSuccess(String translatedText) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.productName.setText(translatedText);
                        }
                    });

                }

                @Override
                public void onFailure(String errorMessage) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.productName.setText(name);
                        }
                    });

                }
            });
        }else{
            holder.productName.setText(name);
        }

        holder.productPrice.setVisibility(View.GONE);

        double discount = productDTO.getDiscount();
        if (discount > 0) {
            holder.discountBadge.setVisibility(View.VISIBLE);

            String discountTxt = String.valueOf(discount) + "% Off";
            holder.discountText.setText(discountTxt);
        } else {
            holder.discountBadge.setVisibility(View.GONE);
        }

        holder.productName.setText(productDTO.getName());
        holder.productCardConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ProductViewActivity.class);
                Gson gson = new Gson();

                intent.putExtra("productDTO", gson.toJson(productDTO));

                v.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return productDTOArrayList.size();
    }

}
