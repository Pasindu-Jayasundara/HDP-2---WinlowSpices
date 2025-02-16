package com.example.winloadmin.dto;

import java.io.Serializable;

public class WeightCategoryDTO implements Serializable {

    private double unit_price;
    private double weight;

    public WeightCategoryDTO() {
    }

    public WeightCategoryDTO(double unit_price, double weight) {
        this.unit_price = unit_price;
        this.weight = weight;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
