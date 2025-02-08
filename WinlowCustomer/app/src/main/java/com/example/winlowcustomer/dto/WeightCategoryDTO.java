package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class WeightCategoryDTO implements Serializable {

    private long weight;
    private long unitPrice;

    public WeightCategoryDTO() {
    }

    public WeightCategoryDTO(long weight, long unitPrice) {
        this.weight = weight;
        this.unitPrice = unitPrice;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }
}
