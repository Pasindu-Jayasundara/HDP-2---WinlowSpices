package com.example.winloadmin.nav;

import static com.example.winloadmin.MainActivity.productDTOList;
import static com.example.winloadmin.nav.DashboardFragment.topSellingProduct;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.winloadmin.R;
import com.example.winloadmin.admin.AddNewAdminFragment;
import com.example.winloadmin.product.AddNewProductFragment;
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
        FragmentContainerView fragmentContainerView = view.findViewById(R.id.fragmentContainerView30);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView30, new ProductSearchFragment())
                .setReorderingAllowed(true)
                .commit();
        fragmentContainerView.setVisibility(View.GONE);

        CardView search = view.findViewById(R.id.searchProductCardView);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragmentContainerView.getVisibility() == View.GONE){
                    fragmentContainerView.setVisibility(View.VISIBLE);
                    fragmentContainerView.setAlpha(0f);
                    fragmentContainerView.animate().alpha(1f).setDuration(1000).start();
                }else{
                    fragmentContainerView.animate().alpha(0f).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            fragmentContainerView.setVisibility(View.GONE);
                        }
                    }).start();
                }
            }
        });

        // all product btn
        FragmentContainerView fragmentContainerView2 = view.findViewById(R.id.fragmentContainerView);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new AllProductFragment())
                .setReorderingAllowed(true)
                .commit();
        fragmentContainerView2.setVisibility(View.GONE);

        CardView all = view.findViewById(R.id.allProductCardView);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragmentContainerView2.getVisibility() == View.GONE){
                    fragmentContainerView2.setVisibility(View.VISIBLE);
                    fragmentContainerView2.setAlpha(0f);
                    fragmentContainerView2.animate().alpha(1f).setDuration(1000).start();
                }else{
                    fragmentContainerView2.animate().alpha(0f).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            fragmentContainerView2.setVisibility(View.GONE);
                        }
                    }).start();
                }

            }
        });

        // add new product btn
        FragmentContainerView fragmentContainerView3 = view.findViewById(R.id.fragmentContainerView40);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView40, new AddNewProductFragment())
                .setReorderingAllowed(true)
                .commit();
        fragmentContainerView3.setVisibility(View.GONE);

        CardView addNew = view.findViewById(R.id.addProductCardView);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragmentContainerView3.getVisibility() == View.GONE){
                    fragmentContainerView3.setVisibility(View.VISIBLE);
                    fragmentContainerView3.setAlpha(0f);
                    fragmentContainerView3.animate().alpha(1f).setDuration(1000).start();
                }else{
                    fragmentContainerView3.animate().alpha(0f).setDuration(400).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            fragmentContainerView3.setVisibility(View.GONE);
                        }
                    }).start();
                }

            }
        });

    }
}