package com.example.winloadmin.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderItemDTO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderRecyclerViewCardInnerRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewCardInnerRecyclerViewAdapter.OrderRecyclerViewCardInnerRecyclerViewHolder>{

    List<OrderItemDTO> orderItemDTOList;

    public OrderRecyclerViewCardInnerRecyclerViewAdapter(List<OrderItemDTO> orderItemDTOList) {
        this.orderItemDTOList = orderItemDTOList;
    }

    @NonNull
    @Override
    public OrderRecyclerViewCardInnerRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflated = layoutInflater.inflate(R.layout.order_search_recyclerview_card_inner_recyclerview_card, parent, false);

        return new OrderRecyclerViewCardInnerRecyclerViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewCardInnerRecyclerViewHolder holder, int position) {

        OrderItemDTO orderItemDTO = orderItemDTOList.get(position);

        String name = orderItemDTO.getName();

        String regex = "\\(([^)]+)\\)";  // Captures anything inside parentheses
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        String weight = matcher.group(1);

        holder.weight.setText(weight);
        holder.qty.setText(String.valueOf(orderItemDTO.getQuantity()));

    }

    @Override
    public int getItemCount() {
        return orderItemDTOList.size();
    }

    public class OrderRecyclerViewCardInnerRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView weight;
        TextView qty;

        public OrderRecyclerViewCardInnerRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            weight = itemView.findViewById(R.id.textView16);
            qty = itemView.findViewById(R.id.textView17);
        }
    }
}
