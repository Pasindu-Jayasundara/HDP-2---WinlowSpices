package com.example.winloadmin.nav;

import static com.example.winloadmin.MainActivity.customerDTOList;
import static com.example.winloadmin.MainActivity.orderCount;
import static com.example.winloadmin.MainActivity.orderDTOList;
import static com.example.winloadmin.MainActivity.productDTOList;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.winloadmin.R;
import com.example.winloadmin.dto.CustomerDTO;
import com.example.winloadmin.dto.OrderDTO;
import com.example.winloadmin.dto.OrderItemDTO;
import com.example.winloadmin.dto.ProductDTO;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<OrderItemDTO> orderItemDTOList;
    public static String topSellingProduct;
//    List<OrderDTO> orderDTOList;
//    List<CustomerDTO> customerDTOList;
//    List<ProductDTO> productDTOList;

    public DashboardFragment(List<OrderItemDTO> orderItemDTOList) {
        this.orderItemDTOList = orderItemDTOList;
//        this.orderDTOList = orderDTOList;
//        this.customerDTOList = customerDTOList;
//        this.productDTOList = productDTOList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!productDTOList.isEmpty()) {
            loadProducts(view);
        }
        if (!customerDTOList.isEmpty()) {
            loadCustomers(view);
        }
        if (!orderItemDTOList.isEmpty()) {
            loadOrders(view);
        }

        loadSalesAndRevenueLineChart(view);
        loadOrderStatusPieChart(view);
        loadTopSellingProductsHorizontalBarChart(view);

    }

    private void loadTopSellingProductsHorizontalBarChart(View view) {

        Map<String, JsonObject> orderMap = new HashMap<>();

        for(OrderItemDTO orderItemDTO:orderItemDTOList){

            String id = orderItemDTO.getId();
            if(orderMap.containsKey(id)){

                JsonObject jsonObject = orderMap.get(id);
                int qty = jsonObject.get("qty").getAsInt();
                jsonObject.addProperty("qty",qty+orderItemDTO.getQuantity());

                orderMap.put(id,jsonObject);

            }else{

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name",orderItemDTO.getName());
                jsonObject.addProperty("qty",orderItemDTO.getQuantity());
                orderMap.put(id,jsonObject);

            }

        }

        // Convert Map to List for sorting
        List<Map.Entry<String, JsonObject>> sortedList = new ArrayList<>(orderMap.entrySet());

        // Sort by quantity in descending order
        sortedList.sort((e1, e2) -> Integer.compare(e2.getValue().get("qty").getAsInt(), e1.getValue().get("qty").getAsInt()));

        // Convert to BarEntry List after sorting
        List<BarEntry> barEntryList = new ArrayList<>();
        List<String> productNameList = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, JsonObject> entry : sortedList) {

            String productName = entry.getValue().get("name").getAsString();
            int quantity = entry.getValue().get("qty").getAsInt();

            barEntryList.add(new BarEntry(index, quantity));
            productNameList.add(productName);

            topSellingProduct = productName;

            index++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntryList,getString(R.string.top_selling));
        barDataSet.setColor(Color.BLUE);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);

        HorizontalBarChart horizontalBarChart = view.findViewById(R.id.horizontalBarChart);
        horizontalBarChart.setData(barData);

        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(productNameList));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        horizontalBarChart.invalidate();


    }

    private void loadOrderStatusPieChart(View view) {

        Map<String, Integer> map = new HashMap<>();
        int totalOrders = 0;

        // Count order statuses
        for (OrderDTO orderDTO : orderDTOList) {

            String orderStatus = orderDTO.getOrder_status();
            map.put(orderStatus, map.getOrDefault(orderStatus, 0) + 1);

        }

        // Calculate total orders
        for (int count : map.values()) {
            totalOrders += count;
        }

        List<PieEntry> pieEntryList = new ArrayList<>();

        // Convert counts to percentages
        for (Map.Entry<String, Integer> entry : map.entrySet()) {

            float percentage = (entry.getValue() / (float) totalOrders) * 100;

            PieEntry pieEntry = new PieEntry(percentage, entry.getKey());
            pieEntryList.add(pieEntry);

        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, getString(R.string.orders_title));
        PieData pieData = new PieData(pieDataSet);

        PieChart pieChart = view.findViewById(R.id.pieChart);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void loadSalesAndRevenueLineChart(View view) {

        List<Entry> salesEntryList = new ArrayList<>();
        List<Entry> revenueEntryList = new ArrayList<>();

        int index = 0;
        for(OrderItemDTO orderItemDTO:orderItemDTOList){

            double amount = orderItemDTO.getAmount();
            int quantity = orderItemDTO.getQuantity();

            Entry salesEntry = new Entry(index,quantity);
            salesEntryList.add(salesEntry);

            Entry revenueEntry = new Entry(index, (float) amount);
            revenueEntryList.add(revenueEntry);

            index++;
        }

        LineDataSet lineSalesDataSet = new LineDataSet(salesEntryList,getString(R.string.sales));
        lineSalesDataSet.setColor(Color.BLUE);
        lineSalesDataSet.setCircleColor(Color.BLUE);
        lineSalesDataSet.setLineWidth(2f);
        lineSalesDataSet.setValueTextSize(10f);

        LineDataSet linRevenueDataSet = new LineDataSet(revenueEntryList,getString(R.string.revenue));
        linRevenueDataSet.setColor(Color.RED);
        linRevenueDataSet.setCircleColor(Color.RED);
        linRevenueDataSet.setLineWidth(2f);
        linRevenueDataSet.setValueTextSize(10f);

        LineData lineData = new LineData();
        lineData.addDataSet(lineSalesDataSet);
        lineData.addDataSet(linRevenueDataSet);

        LineChart lineChart = view.findViewById(R.id.lineChart);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    private void loadOrders(View view) {

        db.collection("order")
//                .where(Filter.notEqualTo("order_status", "Delivered"))
                .orderBy("date_time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        boolean empty = task.getResult().getDocuments().isEmpty();
                        if (!empty) {

                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {

                                OrderDTO orderDTO = documentSnapshot.toObject(OrderDTO.class);
                                orderDTOList.add(orderDTO);

                                List<OrderItemDTO> orderList = orderDTO.getOrder_list();
                                if(orderList!=null){
                                    orderItemDTOList.addAll(orderList);
                                }
                            }

                            orderCount = orderDTOList.size();

                            int size = documentSnapshots.size();
                            TextView productCount = view.findViewById(R.id.textView25);
                            productCount.setText(String.valueOf(size));

                        }

                    }
                });

    }

    private void loadCustomers(View view) {

        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        boolean empty = task.getResult().getDocuments().isEmpty();
                        if (!empty) {

                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {

                                CustomerDTO customerDTO = documentSnapshot.toObject(CustomerDTO.class);
                                customerDTOList.add(customerDTO);
                            }

                            int size = documentSnapshots.size();
                            TextView productCount = view.findViewById(R.id.textView27);
                            productCount.setText(String.valueOf(size));

                        }

                    }
                });

    }

    private void loadProducts(View view) {

        db.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        boolean empty = task.getResult().getDocuments().isEmpty();
                        if (!empty) {

                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {

                                ProductDTO productDTO = documentSnapshot.toObject(ProductDTO.class);
                                productDTO.setId(documentSnapshot.getId());
                                productDTOList.add(productDTO);

                            }

                            int size = documentSnapshots.size();
                            TextView productCount = view.findViewById(R.id.textView29);
                            productCount.setText(String.valueOf(size));

                        }

                    }
                });

    }
}