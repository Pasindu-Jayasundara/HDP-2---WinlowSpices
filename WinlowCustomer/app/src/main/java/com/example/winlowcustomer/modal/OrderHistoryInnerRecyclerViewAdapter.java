package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.MainActivity.language;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.OrderDTO;
import com.example.winlowcustomer.modal.callback.TranslationCallback;

import java.util.List;

public class OrderHistoryInnerRecyclerViewAdapter extends RecyclerView.Adapter<OrderHistoryInnerRecyclerViewAdapter.OrderHistoryInnerRecyclerViewHolder>{

    List<OrderDTO> orderDTOList;
    Activity activity;

    public OrderHistoryInnerRecyclerViewAdapter(List<OrderDTO> orderDTOList, Activity activity) {
        this.orderDTOList = orderDTOList;
        this.activity = activity;
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

        Translate.translateText(orderDTO.getName(), language, new TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.weight.setText(translatedText);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.weight.setText(orderDTO.getName());
                    }
                });
            }
        });

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
