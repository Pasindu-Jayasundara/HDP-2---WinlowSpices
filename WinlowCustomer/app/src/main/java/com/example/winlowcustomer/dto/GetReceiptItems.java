package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class GetReceiptItems implements Serializable {

    private String id;
    private String name;
    private String weight;
    private String unitPrice;
    private String qty;
    private String totalWeightPrice;

    public GetReceiptItems() {
    }

    public GetReceiptItems(String id, String name, String weight, String unitPrice, String qty, String totalWeightPrice) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.unitPrice = unitPrice;
        this.qty = qty;
        this.totalWeightPrice = totalWeightPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotalWeightPrice() {
        return totalWeightPrice;
    }

    public void setTotalWeightPrice(String totalWeightPrice) {
        this.totalWeightPrice = totalWeightPrice;
    }
}
