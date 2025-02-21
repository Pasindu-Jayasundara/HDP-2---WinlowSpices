package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.modal.CartRecyclerViewAdapter.checkoutProductList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.CartActivity;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.CartWeightCategoryDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.WeightCategoryDTO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartCardInnerRecyclerViewAdapter extends RecyclerView.Adapter<CartCardInnerRecyclerViewAdapter.CartCardInnerRecyclerViewHolder>{

//    List<CartDTO> cartDataList;
    List<Map<String,Object>> cartDTOMapWeightCategoryList;
    CartDTO cartDTO;
    ArrayList<ProductDTO> productDTOArrayList;
    TableLayout tableLayout;
//    Context context;

//    public CartCardInnerRecyclerViewAdapter(List<CartDTO> dataList){
//        this.cartDataList = dataList;
//    }
    public CartCardInnerRecyclerViewAdapter(CartDTO cartDTO, Context context, TableLayout tableLayout){
//        Log.i("sendingIn","cartDto: "+new Gson().toJson(cartDTO));

        this.cartDTOMapWeightCategoryList = (List<Map<String, Object>>) cartDTO.getCartDTOMap().get("weight_category");
        this.cartDTO = cartDTO;
//        Log.i("sendingIn","wl 1: "+new Gson().toJson(cartDTO.getCartDTOMap()));
//        Log.i("sendingIn","wl 2: "+new Gson().toJson(cartDTO.getCartDTOMap().get("weight_category")));

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.winlowcustomer.data", Context.MODE_PRIVATE);
        String productJson = sharedPreferences.getString("product", null);
//        Log.i("sendingIn","wl 5: "+productJson);
        Gson gson = new Gson();
        this.productDTOArrayList = gson.fromJson(productJson, new com.google.gson.reflect.TypeToken<ArrayList<ProductDTO>>() {}.getType());
        this.tableLayout = tableLayout;
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

        Map<String, Object> map = cartDTOMapWeightCategoryList.get(position);

        double weight = Double.parseDouble(String.valueOf(map.get("weight")));
        double qty = Double.parseDouble(String.valueOf(map.get("qty")));


        for(ProductDTO productDTO : productDTOArrayList){
            if(productDTO.getReferencePath().equals(cartDTO.getProduct().getReferencePath())){
                cartDTO.setProduct(productDTO);
//                Log.i("sendingIn","unitPrice 3:"+new Gson().toJson(productDTOArrayList));

                break;
            }
        }
        ProductDTO product = cartDTO.getProduct();

        double unitPrice=0.0;
        List<WeightCategoryDTO> weightCategoryDTOList = product.getWeight_category();
        for (WeightCategoryDTO weightCategoryDTO : weightCategoryDTOList) {
//            Log.i("sendingIn","unitPrice 1:"+new Gson().toJson(weightCategoryDTO));

            if(weightCategoryDTO.getWeight() == weight){

                holder.unitPriceTxt.setText("Rs. "+String.valueOf(qty* weightCategoryDTO.getUnitPrice()));

                holder.unitPrice.setText(String.valueOf("Rs. "+weightCategoryDTO.getUnitPrice()));
                unitPrice = weightCategoryDTO.getUnitPrice();
                break;
            }

        }

        holder.weightTxt.setText(String.valueOf(weight+" g"));
        holder.selectedQtyTxt.setText(String.valueOf((int) qty));

        cartDTO.setWeight(holder.weightTxt.getText().toString());

        double finalUnitPrice = unitPrice;
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedQty = Integer.parseInt(holder.selectedQtyTxt.getText().toString());
                selectedQty++;
                holder.selectedQtyTxt.setText(String.valueOf(selectedQty));
                holder.unitPriceTxt.setText("Rs. "+String.valueOf(selectedQty* finalUnitPrice));

//                if(selectedQty > 0){
//                    if(!checkoutProductList.contains(cartDTO)){
//                        checkoutProductList.add(cartDTO);
//                    }
//                }

                for (CartWeightCategoryDTO cartWeightCategoryDTO : cartDTO.getCartWeightCategoryDTOList()) {
                    if(cartWeightCategoryDTO.getWeight() == weight){
                        cartWeightCategoryDTO.setQty(selectedQty);
                        break;
                    }
                }
//                cartDTO.setWeight(holder.weightTxt.getText().toString());
                Log.i("sendingIn","cartDTO: "+new Gson().toJson(cartDTO));

                if(cartDTO.isChecked()){
                    checkoutProductList.remove(cartDTO);
                    checkoutProductList.add(cartDTO);

                    CartActivity.calculatePriceData(tableLayout);
                }

            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedQty = Integer.parseInt(holder.selectedQtyTxt.getText().toString());
                if(selectedQty > 0){
                    selectedQty--;
                    holder.selectedQtyTxt.setText(String.valueOf(selectedQty));
                    holder.unitPriceTxt.setText("Rs. "+String.valueOf(selectedQty* finalUnitPrice));

//                    if(checkoutProductList.contains())
//                    if(selectedQty == 0){

                }

                for (CartWeightCategoryDTO cartWeightCategoryDTO : cartDTO.getCartWeightCategoryDTOList()) {
                    if (cartWeightCategoryDTO.getWeight() == weight) {
                        cartWeightCategoryDTO.setQty(selectedQty);
                        break;
                    }
                }

//                cartDTO.setWeight(holder.weightTxt.getText().toString());
                Log.i("sendingIn","cartDTO: "+new Gson().toJson(cartDTO));

                if(cartDTO.isChecked()){
                    checkoutProductList.remove(cartDTO);
                    checkoutProductList.add(cartDTO);
                    CartActivity.calculatePriceData(tableLayout);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
//        return cartDataList.size();
        return cartDTOMapWeightCategoryList.size();
    }

    public class CartCardInnerRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView weightTxt;
        TextView unitPrice;
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
            unitPrice = itemView.findViewById(R.id.textView30);
        }
    }
}
