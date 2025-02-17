package com.example.winloadmin.model;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.winloadmin.MainActivity;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.AdminDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.AdminRecyclerViewHolder>{

    List<AdminDTO> adminDTOList;

    public AdminRecyclerViewAdapter(List<AdminDTO> adminDTOList) {
        this.adminDTOList = adminDTOList;
    }

    @NonNull
    @Override
    public AdminRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflated = layoutInflater.inflate(R.layout.admin_card, parent, false);

        return new AdminRecyclerViewHolder(inflated);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRecyclerViewHolder holder, int position) {

        AdminDTO adminDTO = adminDTOList.get(position);

        holder.name.setText(adminDTO.getName());
        holder.email.setText(adminDTO.getEmail());

        Glide.with(holder.itemView.getContext())
                .load(adminDTO.getProfileImage())
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(holder.profileImage);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.delete_admin_title)
                        .setMessage(R.string.delete_admin_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteAdmin(adminDTO,v);

                            }
                        })
                        .show();

            }
        });

    }

    private void deleteAdmin(AdminDTO adminDTO, View view) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin").document(adminDTO.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        adminDTOList.remove(adminDTO);

                        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
                        recyclerView.getAdapter().notifyDataSetChanged();

                        Toast.makeText(view.getContext(),R.string.admin_delete_success,Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(),R.string.admin_delete_failed,Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return adminDTOList.size();
    }

    public class AdminRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        ImageButton deleteBtn;
        TextView name;
        TextView email;

        public AdminRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageView8);
            deleteBtn = itemView.findViewById(R.id.imageButton3);
            name = itemView.findViewById(R.id.textView42);
            email = itemView.findViewById(R.id.textView43);
        }
    }
}
