package com.example.winloadmin.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.winloadmin.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {

    private MutableLiveData<List<ProductDTO>> productList = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<String> topSellingProduct = new MutableLiveData<>("");
    private MutableLiveData<Integer> productCount = new MutableLiveData<>(0);

    public LiveData<List<ProductDTO>> getProductList() {
        return productList;
    }
    public LiveData<String> getTopSellingProduct() {
        return topSellingProduct;
    }

    public void updateProductList(List<ProductDTO> list) {
        productList.setValue(list);
    }

    public void updateTopSellingProduct(String name) {
        topSellingProduct.setValue(name);
    }
}
