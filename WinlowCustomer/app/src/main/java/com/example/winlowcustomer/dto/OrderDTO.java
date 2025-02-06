package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class OrderDTO implements Serializable {

    private double cost;
    private LocalDate dateTime;
    private String orderStatus;
    private String paymentMethod;
    private String productId;
    private int qty;
    private String user;

    public OrderDTO() {
    }

    public OrderDTO(double cost, LocalDate dateTime, String orderStatus, String paymentMethod, String productId, int qty, String user) {
        this.cost = cost;
        this.dateTime = dateTime;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.productId = productId;
        this.qty = qty;
        this.user = user;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
