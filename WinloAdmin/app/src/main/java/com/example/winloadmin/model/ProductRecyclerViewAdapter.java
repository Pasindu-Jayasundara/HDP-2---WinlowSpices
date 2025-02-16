package com.example.winloadmin.model;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.winloadmin.MainActivity.orderDTOList;
import static com.example.winloadmin.MainActivity.productDTOList;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winloadmin.MainActivity;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.dto.OrderItemDTO;
import com.example.winloadmin.dto.ProductDTO;
import com.example.winloadmin.product.UpdateProductFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductRecyclerViewHolder>{

    String productId;

    @NonNull
    @Override
    public ProductRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_search_recyclerview_card,parent,false);

        return new ProductRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerViewHolder holder, int position) {

        ProductDTO productDTO = productDTOList.get(position);

        holder.productName.setText(productDTO.getName());
        holder.category.setText(productDTO.getCategory());

        // image
        Glide.with(holder.itemView.getContext())
                .load(productDTO.getImage_path())
                .placeholder(R.drawable.product_image)
                .error(R.drawable.product_image)
                .into(holder.productImage);

        // delete btn
        productId = productDTO.getId();
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_txt)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean isAbleToDelete = checkDeleteAbility(productDTO);
                                if(isAbleToDelete){
                                    deleteProduct(productDTO,v.getContext());
                                }else{
                                    Toast.makeText(v.getContext(),R.string.cannot_be_deleted,Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();

            }
        });

        // update btn
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = ((MainActivity)v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView3,new UpdateProductFragment(productDTO,holder.recyclerView))
                        .setReorderingAllowed(true)
                        .commit();

            }
        });

    }

    private boolean checkDeleteAbility(ProductDTO deletingProductDTO) {

        boolean isNotFound = true;

        for(OrderDTO orderDTO : orderDTOList){

            List<OrderItemDTO> orderList = orderDTO.getOrder_list();
            for (OrderItemDTO orderItemDTO:orderList){

                if(orderItemDTO.getId().equals(deletingProductDTO.getId())){

                    isNotFound = false;
                    break;
                }

            }

        }

        return isNotFound;

    }

    private void deleteProduct(ProductDTO productDTO, Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product")
                .document(productId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        productDTOList.remove(productDTO);
                        notifyDataSetChanged();

                        Toast.makeText(context,R.string.delete_success, Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context,R.string.delete_failed, Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public int getItemCount() {
        return productDTOList.size();
    }

    public class ProductRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productName;
        TextView category;
        RecyclerView recyclerView;
        Button deleteBtn;
        Button updateBtn;

        public ProductRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.imageView3);
            productName = itemView.findViewById(R.id.textView7);
            category = itemView.findViewById(R.id.textView8);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            deleteBtn = itemView.findViewById(R.id.button4);
            updateBtn = itemView.findViewById(R.id.button7);
        }
    }
}
