package com.example.winlowcustomer.modal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.CartWeightCategoryDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;

import java.util.List;

public class CartCardInnerRecyclerViewAdapter extends RecyclerView.Adapter<CartCardInnerRecyclerViewAdapter.CartCardInnerRecyclerViewHolder>{

    List<CartDTO> cartDataList;

    public CartCardInnerRecyclerViewAdapter(List<CartDTO> dataList){
        this.cartDataList = dataList;
    }

    @NonNull
    @Override
    public CartCardInnerRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflated = layoutInflater.inflate(R.layout.cart_inner_list_card_layout, parent, false);

        return new CartCardInnerRecyclerViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(@NonNull CartCardInnerRecyclerViewHolder holder, int position) {

        CartDTO cartDTO = cartDataList.get(position);

        double weight = cartDTO.getCartWeightCategoryDTOList().get(position).getWeight();
        double qty = cartDTO.getCartWeightCategoryDTOList().get(position).getQty();

        ProductDTO product = cartDTO.getProduct();

        List<WeightCategoryDTO> weightCategoryDTOList = product.getWeightCategoryDTOList();
        for (WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList) {

            if(weightCategoryDTO.getWeight() == weight){
                holder.unitPriceTxt.setText(String.valueOf("Rs. "+weightCategoryDTO.getUnitPrice()));
                break;
            }

        }

        holder.weightTxt.setText(String.valueOf(weight+" g"));
        holder.selectedQtyTxt.setText(String.valueOf(qty));

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedQty = Integer.parseInt(holder.selectedQtyTxt.getText().toString());
                selectedQty++;
                holder.selectedQtyTxt.setText(String.valueOf(selectedQty));

                for (CartWeightCategoryDTO cartWeightCategoryDTO : cartDTO.getCartWeightCategoryDTOList()) {
                    if(cartWeightCategoryDTO.getWeight() == weight){
                        cartWeightCategoryDTO.setQty(selectedQty);
                        break;
                    }
                }

            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedQty = Integer.parseInt(holder.selectedQtyTxt.getText().toString());
                if(selectedQty > 1){
                    selectedQty--;
                    holder.selectedQtyTxt.setText(String.valueOf(selectedQty));
                }

                for (CartWeightCategoryDTO cartWeightCategoryDTO : cartDTO.getCartWeightCategoryDTOList()) {
                    if (cartWeightCategoryDTO.getWeight() == weight) {
                        cartWeightCategoryDTO.setQty(selectedQty);
                        break;
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartDataList.size();
    }

    public class CartCardInnerRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView weightTxt;
        TextView unitPriceTxt;
        TextView selectedQtyTxt;
        Button plusBtn;
        Button minusBtn;

        public CartCardInnerRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            weightTxt = itemView.findViewById(R.id.textView74);
            unitPriceTxt = itemView.findViewById(R.id.textView76);
            selectedQtyTxt = itemView.findViewById(R.id.textView80);
            plusBtn = itemView.findViewById(R.id.button17);
            minusBtn = itemView.findViewById(R.id.button18);
        }
    }
}
