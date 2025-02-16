package com.example.winloadmin.dto;

import java.io.Serializable;
import java.util.List;

public class OrderDTO implements Serializable {

    private long date_time;
    private String order_id;
    private String order_status;
    private String payment_method;
    private String user_id;
    private List<OrderItemDTO> order_list;

    public OrderDTO() {
    }

    public OrderDTO(long date_time, String order_id, String order_status, String payment_method, String user_id, List<OrderItemDTO> order_list) {
        this.date_time = date_time;
        this.order_id = order_id;
        this.order_status = order_status;
        this.payment_method = payment_method;
        this.user_id = user_id;
        this.order_list = order_list;
    }

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<OrderItemDTO> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<OrderItemDTO> order_list) {
        this.order_list = order_list;
    }
}
