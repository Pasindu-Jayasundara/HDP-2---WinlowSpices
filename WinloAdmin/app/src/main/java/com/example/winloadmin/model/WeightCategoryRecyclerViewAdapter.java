package com.example.winloadmin.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.WeightCategoryDTO;

import java.util.List;

public class WeightCategoryRecyclerViewAdapter extends RecyclerView.Adapter<WeightCategoryRecyclerViewAdapter.WeightCategoryRecyclerViewHolder>{

    List<WeightCategoryDTO> weightCategoryList;

    public WeightCategoryRecyclerViewAdapter(List<WeightCategoryDTO> weightCategoryList) {
        this.weightCategoryList = weightCategoryList;
    }

    @NonNull
    @Override
    public WeightCategoryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_search_recyclerview_card_inner_recyclerview_card,parent,false);

        return new WeightCategoryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightCategoryRecyclerViewHolder holder, int position) {

        WeightCategoryDTO weightCategoryDTO = weightCategoryList.get(position);

        holder.weight.setText(String.valueOf(weightCategoryDTO.getWeight())+" g");
        holder.price.setText("Rs. "+String.valueOf(weightCategoryDTO.getUnit_price()));

    }

    @Override
    public int getItemCount() {
        return weightCategoryList.size();
    }

    public class WeightCategoryRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView weight;
        TextView price;

        public WeightCategoryRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            weight = itemView.findViewById(R.id.textView9);
            price = itemView.findViewById(R.id.textView10);
        }
    }
}
