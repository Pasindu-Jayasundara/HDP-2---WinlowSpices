package com.example.winlowcustomer.dto;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class ProductDTO implements Serializable {

    private String id; // doc id
    private String category;
    private String name;
    private String stock;
    private double discount;
    private List<WeightCategoryDTO> weightCategoryDTOList;

    private String imagePath;

    public ProductDTO() {
    }

    public ProductDTO(String id, String category, String name, String stock, double discount, List<WeightCategoryDTO> weightCategoryDTOList , String imagePath) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.stock = stock;
        this.discount = discount;
        this.weightCategoryDTOList = weightCategoryDTOList;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public @Nullable List<WeightCategoryDTO> getWeightCategoryDTOList() {
        return weightCategoryDTOList;
    }

    public void setWeightCategoryDTOList(List<WeightCategoryDTO> weightCategoryDTOList) {
        this.weightCategoryDTOList = weightCategoryDTOList;
    }
}
