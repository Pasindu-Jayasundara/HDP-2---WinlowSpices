package com.example.winlowcustomer.modal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SingleProductViewRecyclerViewAdapter extends RecyclerView.Adapter<SingleProductViewRecyclerViewAdapter.SingleProductViewRecyclerViewHolder> {

    public static HashMap<Double, Integer> weightHashMap;
//    public static ProductDTO product;

    List<WeightCategoryDTO> weightCategoryDTOArrayList;

    public class SingleProductViewRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView weight;
        TextView weightUnitPrice;
        TextView selectedQty;
        ImageButton weightMinus;
        ImageButton weightPlus;

        public SingleProductViewRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            weight = itemView.findViewById(R.id.textView60);
            weightUnitPrice = itemView.findViewById(R.id.textView61);
            selectedQty = itemView.findViewById(R.id.textView62);
            weightMinus = itemView.findViewById(R.id.imageButton25);
            weightPlus = itemView.findViewById(R.id.imageButton24);
        }
    }

    public SingleProductViewRecyclerViewAdapter(ProductDTO productDTO) {
        this.weightCategoryDTOArrayList = productDTO.getWeightCategoryDTOList();
//        this.product = productDTO;
    }

    @NonNull
    @Override
    public SingleProductViewRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_weight_category_layout, parent, false);
        return new SingleProductViewRecyclerViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleProductViewRecyclerViewHolder holder, int position) {

        WeightCategoryDTO weightCategoryDTO = weightCategoryDTOArrayList.get(position);

        String weight = String.valueOf(weightCategoryDTO.getWeight()) + (weightCategoryDTO.getWeight() > 1000 ? "Kg" : "g");
        holder.weight.setText(weight);

        String unitPrice = "Rs. " + String.valueOf(weightCategoryDTO.getUnitPrice());
        holder.weightUnitPrice.setText(unitPrice);

//        if(weightHashMap != null && weightHashMap.containsKey(weightCategoryDTO.getWeight())){
//            holder.selectedQty.setText(String.valueOf(weightHashMap.get(weightCategoryDTO.getWeight())));
//        }

//        holder.selectedQty.setText("0");

        holder.weightMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(holder.selectedQty.getText().toString());
                if (qty > 0) {
                    qty--;
                    holder.selectedQty.setText(String.valueOf(qty));

                    catList(qty,weightCategoryDTO.getWeight());
                } else {
                    Toast.makeText(v.getContext(), R.string.weight_zero_qty, Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.weightPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(holder.selectedQty.getText().toString());
                qty++;
                holder.selectedQty.setText(String.valueOf(qty));

                catList(qty,weightCategoryDTO.getWeight());

            }
        });

    }

    @Override
    public int getItemCount() {
        return weightCategoryDTOArrayList.size();
    }

    private void catList(int qty,double weight){

        if (weightHashMap == null) {
            weightHashMap = new HashMap<>();
        }

        if(qty == 0){
            weightHashMap.remove(weight);
            return;
        }
        weightHashMap.put(weight,qty);
    }

}
