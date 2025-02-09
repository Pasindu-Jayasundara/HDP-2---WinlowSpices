package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class CartWeightCategoryDTO implements Serializable {

    private long weight;
    private int qty;

    public CartWeightCategoryDTO() {
    }

    public CartWeightCategoryDTO(long weight, int qty) {
        this.weight = weight;
        this.qty = qty;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
