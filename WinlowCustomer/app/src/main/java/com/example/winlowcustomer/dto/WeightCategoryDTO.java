package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class WeightCategoryDTO implements Serializable {

    private double weight;
    private double unitPrice;

    public WeightCategoryDTO() {
    }

    public WeightCategoryDTO(double weight, double unitPrice) {
        this.weight = weight;
        this.unitPrice = unitPrice;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
