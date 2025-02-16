package com.example.winloadmin.dto;

import java.io.Serializable;
import java.util.List;

public class CustomerDTO implements Serializable {

    private String id;
    private List<String> address;
    private String email;
    private String mobile;
    private String name;
    private List<String> order_history;
    private String profile_image;

    public CustomerDTO() {
    }

    public CustomerDTO(String id, List<String> address, String email, String mobile, String name, List<String> order_history, String profile_image) {
        this.id = id;
        this.address = address;
        this.email = email;
        this.mobile = mobile;
        this.name = name;
        this.order_history = order_history;
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

    public List<String> getOrder_history() {
        return order_history;
    }

    public void setOrder_history(List<String> order_history) {
        this.order_history = order_history;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
