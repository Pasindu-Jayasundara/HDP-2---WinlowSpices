package com.example.winlowcustomer.modal;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.R;
import com.example.winlowcustomer.modal.callback.GetAddressCallback;

import java.util.List;

public class AddressLoadingRecyclerViewAdapter extends RecyclerView.Adapter<AddressLoadingRecyclerViewAdapter.AddressLoadingHolder>{

    List<String> addressList;
    Context context;

    public AddressLoadingRecyclerViewAdapter(List<String> addressList, Context context) {
        this.addressList = addressList;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressLoadingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_card_layout,parent,false);
        return new AddressLoadingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressLoadingHolder holder, int position) {

        holder.checkBox.setText(addressList.get(position));
        holder.checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setMessage(R.string.delete_address_message)
                        .setTitle(R.string.delete_address)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {

                            AddressHandling.deleteAddress(addressList.get(position), context, new GetAddressCallback() {
                                @Override
                                public void onAddressDeleted(boolean deleted) {

                                    addressList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,addressList.size());

                                }
                            });

                        }).show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressLoadingHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        public AddressLoadingHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox2);
        }
    }
}
