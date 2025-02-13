package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class OrderHistoryDTO implements Serializable {

    private String order_id;
    private long date_time;
    private String order_status;
    private String payment_method;
    private String user_id;
    private List<OrderDTO> order_list;

    public OrderHistoryDTO() {
    }

    public OrderHistoryDTO(long date_time, String order_status, String payment_method, String user_id, List<OrderDTO> order_list,String order_id) {
        this.date_time = date_time;
        this.order_status = order_status;
        this.payment_method = payment_method;
        this.user_id = user_id;
        this.order_list = order_list;
        this.order_id = order_id;
    }

    public long getDate_time() {
        return date_time;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
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

    public List<OrderDTO> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<OrderDTO> order_list) {
        this.order_list = order_list;
    }
}
