package com.example.winlowcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winlowcustomer.dto.OrderHistoryDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.OrderHistoryRecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        if(userJson == null){

            Toast.makeText(getApplicationContext(),R.string.login_first,Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(OrderHistoryActivity.this,HomeActivity.class);
            startActivity(intent);

        }else{

            Gson gson = new Gson();
            UserDTO user = gson.fromJson(userJson, UserDTO.class);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(user.getId()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if(documentSnapshot.exists()){


                                List<String> orderHistoryList = (List<String>) documentSnapshot.get("order_history");
//                                List<String> documentIdList = new ArrayList<>();

                                Log.i("cartList",new Gson().toJson(orderHistoryList));

//                                if (cartList != null) {
//                                    for (Object item : cartList) {
//                                        if (item instanceof String) {
//                                            documentIdList.add((String) item);
//                                        } else if (item instanceof Map) {
////                                            // Assuming the document ID is stored in a field like "id" inside the map
//                                            Map<String, Object> map = (Map<String, Object>) item;
////                                            if (map.containsKey("id")) {
////                                                documentIdList.add(map.get("id").toString());
////                                            }
////                                        }
//                                    }
//                                }

                                if (orderHistoryList!=null && !orderHistoryList.isEmpty()) {
                                    db.collection("order")
                                            .whereIn(FieldPath.documentId(), orderHistoryList)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<OrderHistoryDTO> orderHistoryDTOList = new ArrayList<>();
                                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                        OrderHistoryDTO orderHistoryDTO = document.toObject(OrderHistoryDTO.class);
                                                        orderHistoryDTOList.add(orderHistoryDTO);
                                                    }

                                                    RecyclerView recyclerView = findViewById(R.id.orderHistoryRecyclerView);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(OrderHistoryActivity.this, RecyclerView.VERTICAL, false));
                                                    recyclerView.setAdapter(new OrderHistoryRecyclerViewAdapter(orderHistoryDTOList));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(),R.string.something_wrong,Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }
}