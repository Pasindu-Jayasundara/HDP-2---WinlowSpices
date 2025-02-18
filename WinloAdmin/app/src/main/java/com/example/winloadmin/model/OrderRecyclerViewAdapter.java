package com.example.winloadmin.model;

import static com.example.winloadmin.MainActivity.orderDTOList;
import static com.example.winloadmin.order.OrderSearchFragment.recyclerViewOrderSearchFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.dto.OrderItemDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
//        holder.productName.setText(orderDTO.getOrder_list().get(0).getName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = simpleDateFormat.format(orderDTO.getDate_time());
        holder.orderDateTime.setText(date);

        List<OrderItemDTO> orderList = orderDTO.getOrder_list();
        OrderRecyclerViewCardInnerRecyclerViewAdapter orrvcirvadapter = new OrderRecyclerViewCardInnerRecyclerViewAdapter(orderList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext(),LinearLayoutManager.VERTICAL,false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(orrvcirvadapter);

        // order status
        LayoutInflater layoutInflater = LayoutInflater.from(holder.itemView.getContext());
        View inflated = layoutInflater.inflate(R.layout.fragment_order_status_update, holder.itemView.findViewById(R.id.orderStatusConstraintLayout),false);

        // spinner
        Spinner spinner = inflated.findViewById(R.id.spinner3);

        List<String> list = new ArrayList<>();
        list.add(holder.itemView.getContext().getString(R.string.pending));
        list.add(holder.itemView.getContext().getString(R.string.ready_to_deliver));
        list.add(holder.itemView.getContext().getString(R.string.delivering));
        list.add(holder.itemView.getContext().getString(R.string.delivered));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                list
        );

        int index = list.indexOf(orderDTO.getOrder_status());

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(index);

        // update btn
        Button btn = inflated.findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDatabase(inflated,orderDTO);

            }
        });

        holder.frameLayout.addView(inflated);

    }

    @Override
    public int getItemCount() {
        return orderDTOList.size();
    }

    public class OrderRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView orderId;
//        TextView productName;
        TextView orderDateTime;
        RecyclerView recyclerView;
        FrameLayout frameLayout;

        public OrderRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.textView24);
//            productName = itemView.findViewById(R.id.textView15);
            orderDateTime = itemView.findViewById(R.id.textView20);
            recyclerView = itemView.findViewById(R.id.recyclerView2);
            frameLayout = itemView.findViewById(R.id.orderstatusChangeFrameLayout);
        }
    }

    private void updateDatabase(View view,OrderDTO orderDTO2) {

        Spinner spinner = view.findViewById(R.id.spinner3);
        String newStatus = spinner.getSelectedItem().toString();

        if(newStatus.equals(orderDTO2.getOrder_status())){
            return;
        }

        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        db.collection("order")
                .document(orderDTO2.getOrder_id())
                .update("order_status",newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(view.getContext(), R.string.order_status_updated, Toast.LENGTH_SHORT).show();

//                        orderStatus = newStatus;
                        orderDTOList.forEach(orderDTO -> {
                            if(orderDTO.getOrder_id().equals(orderDTO2.getOrder_id())){
                                orderDTO.setOrder_status(newStatus);
                            }
                        });
                        recyclerViewOrderSearchFragment.getAdapter().notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), R.string.order_status_update_failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
