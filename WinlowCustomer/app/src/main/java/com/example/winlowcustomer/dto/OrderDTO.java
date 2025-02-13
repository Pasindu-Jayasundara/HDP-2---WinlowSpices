package com.example.winlowcustomer.dto;

import java.io.Serializable;

public class OrderDTO implements Serializable {

    private double amount;
    private String id; //product
    private int quantity;
    private String name;
    private double weight;

    public OrderDTO() {
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public OrderDTO(double amount, String id, int quantity, String name, double weight) {
        this.amount = amount;
        this.id = id;
        this.quantity = quantity;
        this.name = name;
        this.weight = weight;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
