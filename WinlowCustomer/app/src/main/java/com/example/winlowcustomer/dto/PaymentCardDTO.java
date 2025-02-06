package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class PaymentCardDTO implements Serializable {

    private String cvv;
    private LocalDate expiryDate;
    private String number;

    public PaymentCardDTO() {
    }

    public PaymentCardDTO(String cvv, LocalDate expiryDate, String number) {
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.number = number;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
