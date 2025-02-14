package com.example.winlowcustomer.modal;

import static com.example.winlowcustomer.MainActivity.language;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.dto.OrderHistoryDTO;
import com.example.winlowcustomer.modal.callback.TranslationCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryRecyclerViewAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerViewAdapter.OrderHistoryViewHolder>{

    List<OrderHistoryDTO> orderHistoryDTOList;
    Activity activity;

    public OrderHistoryRecyclerViewAdapter(List<OrderHistoryDTO> orderHistoryDTOList, Activity activity) {
        this.orderHistoryDTOList = orderHistoryDTOList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_history_card,parent,false);

        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {

        OrderHistoryDTO orderHistoryDTO = orderHistoryDTOList.get(position);

        holder.orderId.setText(orderHistoryDTO.getOrder_id());

        Translate.translateText(orderHistoryDTO.getOrder_status(), language, new TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String status = holder.itemView.getContext().getString(R.string.order_status) +" "+ translatedText;
                        holder.status.setText(status);
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String status = holder.itemView.getContext().getString(R.string.order_status) +" "+ orderHistoryDTO.getOrder_status();
                        holder.status.setText(status);
                    }
                });
            }
        });

        long dateTime = orderHistoryDTO.getDate_time();
        Date date = new Date(dateTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.orderDate.setText(formattedDate);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext(),RecyclerView.VERTICAL,false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        OrderHistoryInnerRecyclerViewAdapter orderHistoryInnerRecyclerViewAdapter = new OrderHistoryInnerRecyclerViewAdapter(orderHistoryDTO.getOrder_list(),activity);
        holder.recyclerView.setAdapter(orderHistoryInnerRecyclerViewAdapter);

    }

    @Override
    public int getItemCount() {
        return orderHistoryDTOList.size();
    }

    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder{

        TextView orderId;
        TextView orderDate;
        RecyclerView recyclerView;
        TextView status;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.textView17);
            orderDate = itemView.findViewById(R.id.textView32);
            recyclerView = itemView.findViewById(R.id.recyclerViewOrderHistoryCard);
            status = itemView.findViewById(R.id.textView87);
        }
    }
}
