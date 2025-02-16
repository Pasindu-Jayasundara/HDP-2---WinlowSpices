package com.example.winloadmin.nav;

import static com.example.winloadmin.MainActivity.productDTOList;
import static com.example.winloadmin.nav.DashboardFragment.topSellingProduct;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.winloadmin.R;
import com.example.winloadmin.product.AllProductFragment;
import com.example.winloadmin.product.ProductSearchFragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ProductFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // top selling product name
        TextView topSellingProductView = view.findViewById(R.id.textView5);
        topSellingProductView.setText(topSellingProduct);

        // product count
        TextView productCount = view.findViewById(R.id.textView3);
        productCount.setText(String.valueOf(productDTOList.size()));

        // search product btn
        CardView search = view.findViewById(R.id.searchProductCardView);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView30, new ProductSearchFragment())
                        .setReorderingAllowed(true)
                        .commit();

            }
        });

        // all product btn
        CardView all = view.findViewById(R.id.allProductCardView);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new AllProductFragment())
                        .setReorderingAllowed(true)
                        .commit();

            }
        });

        // add new product btn
        CardView addNew = view.findViewById(R.id.addProductCardView);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView40, new AllProductFragment())
                        .setReorderingAllowed(true)
                        .commit();

            }
        });

    }
}