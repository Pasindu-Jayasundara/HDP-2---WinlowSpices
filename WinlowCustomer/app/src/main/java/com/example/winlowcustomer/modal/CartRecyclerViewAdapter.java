package com.example.winlowcustomer.modal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.CartActivity;
import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.CartDTO;
import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.callback.GetDataRemovedNotified;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartRecyclerViewHolder>{

    public static List<CartDTO> checkoutProductList = new ArrayList<>();
    List<CartDTO> cartDTOList;
    UserDTO userDto;

    public class CartRecyclerViewHolder extends RecyclerView.ViewHolder{

        CheckBox productName;
        TextView remove;

        public CartRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.checkBox);
            remove = itemView.findViewById(R.id.textView75);
        }
    }

    public CartRecyclerViewAdapter(List<CartDTO> cartDTOList, UserDTO userDTO) {
        this.cartDTOList = cartDTOList;
        this.userDto = userDTO;
    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.CartRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflated = layoutInflater.inflate(R.layout.cart_card_layout, parent, false);

        return new CartRecyclerViewAdapter.CartRecyclerViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewAdapter.CartRecyclerViewHolder holder, int position) {

        CartDTO cartDTO = cartDTOList.get(position);

        holder.productName.setText(cartDTO.getProduct().getName());
        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.productName.setChecked(!holder.productName.isChecked());

                if(holder.productName.isChecked()){
                    checkoutProductList.add(cartDTO);
                }else{
                    checkoutProductList.remove(cartDTO);
                }

                TableLayout tableLayout = v.getRootView().findViewById(R.id.tableLayout);
                if(checkoutProductList.isEmpty()){
                    CartActivity.hideCheckout(tableLayout);
                }else{
                    CartActivity.showChekout(tableLayout);
                }

            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.remove.setText(R.string.cart_removing);
                holder.remove.setEnabled(false);
                holder.remove.setClickable(false);


                // remove from firebase
                removeCartItemFromFirebase(cartDTO.getCartDTOMap(), new GetDataRemovedNotified() {
                    @Override
                    public void onDataRemoved(Boolean removed) {

                        if(removed){
                            holder.remove.setText(R.string.cart_removed);
                            holder.remove.setEnabled(true);
                            holder.remove.setClickable(true);

                            cartDTOList.remove(position);
                            notifyDataSetChanged();

                            Toast.makeText(v.getContext(), R.string.cart_removed_success, Toast.LENGTH_SHORT).show();

                        }else{

                            holder.remove.setText(R.string.cart_remove);
                            holder.remove.setEnabled(true);
                            holder.remove.setClickable(true);

                            Toast.makeText(v.getContext(), R.string.cart_removed_failed, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartDTOList.size();
    }


    private void removeCartItemFromFirebase(Map<String,Object> cartDTOMap, GetDataRemovedNotified getDataRemovedNotified) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(userDto.getId())
                .update("cart", FieldValue.arrayRemove(cartDTOMap))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        getDataRemovedNotified.onDataRemoved(true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        getDataRemovedNotified.onDataRemoved(false);

                    }
                });

    }

}
