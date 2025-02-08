package com.example.winlowcustomer.modal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.ProductDTO;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    ArrayList<ProductDTO> productDTOArrayList;

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        FrameLayout discountBadge;
        TextView discountText;
        TextView productName;
        TextView productPrice;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.imageView12);
            discountBadge = itemView.findViewById(R.id.discountBadgeFrameLayout);
            discountText = itemView.findViewById(R.id.textView58);
            productName = itemView.findViewById(R.id.textView53);
            productPrice = itemView.findViewById(R.id.textView54);
        }
    }

    public RecyclerViewAdapter(ArrayList<ProductDTO> productDTOArrayList) {
        this.productDTOArrayList = productDTOArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View productCard = layoutInflater.inflate(R.layout.product_card_layout,parent,false);

        return new RecyclerViewHolder(productCard);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        ProductDTO productDTO = productDTOArrayList.get(position);

        String imageUrl = productDTO.getImagePath();
        Glide.with(holder.productImage.getContext()) // Use context
                .load(imageUrl) // Load image URL
                .placeholder(R.drawable.product_placeholder) // Optional: placeholder image
                .error(R.drawable.product_placeholder) // Optional: error image
                .into(holder.productImage); // Set image into ImageView

        holder.productName.setText(productDTO.getName());
        holder.productPrice.setVisibility(View.GONE);

        double discount = productDTO.getDiscount();
        if(discount > 0){
            holder.discountBadge.setVisibility(View.VISIBLE);

            String discountTxt = String.valueOf(discount)+"% Off";
            holder.discountText.setText(discountTxt);
        }else{
            holder.discountBadge.setVisibility(View.GONE);
        }

        holder.productName.setText(productDTO.getName());

    }

    @Override
    public int getItemCount() {
        return productDTOArrayList.size();
    }

}
