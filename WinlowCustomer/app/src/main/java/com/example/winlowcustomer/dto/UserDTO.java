package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.util.List;

public class UserDTO implements Serializable {

    private String id;
    private List<String> address;
//    private List<String> cart;
    private String email;
    private String mobile;
    private String name;
    private List<String> orderHistory;
    private List<PaymentCardDTO> paymentCard;
    private String profile_image;

    public UserDTO() {
    }

//    List<String> cart,
    public UserDTO(String id, List<String> address,  String email, String mobile, String name, List<String> orderHistory, List<PaymentCardDTO> paymentCard, String profile_image) {
        this.id = id;
        this.address = address;
//        this.cart = cart;
        this.email = email;
        this.mobile = mobile;
        this.name = name;
        this.orderHistory = orderHistory;
        this.paymentCard = paymentCard;
        this.profile_image = profile_image;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

//    public List<String> getCart() {
//        return cart;
//    }

//    public void setCart(List<String> cart) {
//        this.cart = cart;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<String> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public List<PaymentCardDTO> getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(List<PaymentCardDTO> paymentCard) {
        this.paymentCard = paymentCard;
    }
}
