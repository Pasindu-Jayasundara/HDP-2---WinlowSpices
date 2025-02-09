package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class CartWeightCategoryDTO implements Serializable {

    private double weight;
    private int qty;

    public CartWeightCategoryDTO() {
    }

    public CartWeightCategoryDTO(long weight, int qty) {
        this.weight = weight;
        this.qty = qty;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
