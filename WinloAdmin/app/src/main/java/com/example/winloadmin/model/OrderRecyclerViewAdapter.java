package com.example.winloadmin.model;

import static com.example.winloadmin.MainActivity.orderDTOList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winloadmin.MainActivity;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.dto.OrderItemDTO;
import com.example.winloadmin.order.OrderSearchFragment;
import com.example.winloadmin.order.OrderStatusUpdateFragment;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderRecyclerViewHolder>{

//    List<OrderDTO> orderDTOList;
    FragmentManager fragmentManager;

    public OrderRecyclerViewAdapter(FragmentManager fragmentManager) {
//        this.orderDTOList = orderDTOList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public OrderRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflated = layoutInflater.inflate(R.layout.order_search_recyclerview_card, parent, false);

        return new OrderRecyclerViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewHolder holder, int position) {

        OrderDTO orderDTO = orderDTOList.get(position);

        holder.orderId.setText(orderDTO.getOrder_id());
        holder.productName.setText(orderDTO.getOrder_list().get(0).getName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = simpleDateFormat.format(orderDTO.getDate_time());
        holder.orderDateTime.setText(date);

        List<OrderItemDTO> orderList = orderDTO.getOrder_list();
        OrderRecyclerViewCardInnerRecyclerViewAdapter orrvcirvadapter = new OrderRecyclerViewCardInnerRecyclerViewAdapter(orderList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.VERTICAL,false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(orrvcirvadapter);

        // order status
        fragmentManager.beginTransaction()
                .replace(holder.fragmentContainerView.getId(), new OrderStatusUpdateFragment(orderDTO.getOrder_status(),orderDTO.getOrder_id()))
                .setReorderingAllowed(true)
                .commit();


    }

    @Override
    public int getItemCount() {
        return orderDTOList.size();
    }

    public class OrderRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView orderId;
        TextView productName;
        TextView orderDateTime;
        RecyclerView recyclerView;
        FragmentContainerView fragmentContainerView;

        public OrderRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.textView24);
            productName = itemView.findViewById(R.id.textView15);
            orderDateTime = itemView.findViewById(R.id.textView20);
            recyclerView = itemView.findViewById(R.id.recyclerView2);
            fragmentContainerView = itemView.findViewById(R.id.fragmentContainerView2);
        }
    }
}
