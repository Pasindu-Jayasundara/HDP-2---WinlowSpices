package com.example.winloadmin.dto;

import java.io.Serializable;

public class OrderItemDTO implements Serializable {

    private String id; // product id
    private double amount;
    private String name;
    private int quantity;

    public OrderItemDTO() {
    }

    public OrderItemDTO(String id, double amount, String name, int quantity) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
