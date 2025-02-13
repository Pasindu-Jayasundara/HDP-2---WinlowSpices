package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class WeightCategoryDTO implements Serializable {

    private double weight;
    private double unit_price;

    public WeightCategoryDTO() {
    }

    public WeightCategoryDTO(double weight, double unitPrice) {
        this.weight = weight;
        this.unit_price = unitPrice;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getUnitPrice() {
        return unit_price;
    }

    public void setUnitPrice(double unitPrice) {
        this.unit_price = unitPrice;
    }
}
