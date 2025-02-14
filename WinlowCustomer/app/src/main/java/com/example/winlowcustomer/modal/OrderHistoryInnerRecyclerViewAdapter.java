package com.example.winlowcustomer.modal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.OrderDTO;

import java.util.List;

public class OrderHistoryInnerRecyclerViewAdapter extends RecyclerView.Adapter<OrderHistoryInnerRecyclerViewAdapter.OrderHistoryInnerRecyclerViewHolder>{

    List<OrderDTO> orderDTOList;

    public OrderHistoryInnerRecyclerViewAdapter(List<OrderDTO> orderDTOList) {
        this.orderDTOList = orderDTOList;
    }

    @NonNull
    @Override
    public OrderHistoryInnerRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_inner_card_layout,parent,false);
        return new OrderHistoryInnerRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryInnerRecyclerViewHolder holder, int position) {

        OrderDTO orderDTO = orderDTOList.get(position);

        holder.weight.setText(String.valueOf(orderDTO.getName()));
        holder.qty.setText(String.valueOf(orderDTO.getQuantity()));
        holder.amount.setText("Rs. "+String.valueOf(orderDTO.getAmount()));

    }

    @Override
    public int getItemCount() {
        return orderDTOList.size();
    }

    public class OrderHistoryInnerRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView weight;
        TextView qty;
        TextView amount;

        public OrderHistoryInnerRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            weight = itemView.findViewById(R.id.textView85);
            qty = itemView.findViewById(R.id.textView84);
            amount = itemView.findViewById(R.id.textView86);
        }
    }
}
