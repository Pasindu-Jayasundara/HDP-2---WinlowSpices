package com.example.winloadmin.dto;

import java.io.Serializable;
import java.util.List;

public class ProductDTO implements Serializable {

    private String id;
    private String category;
    private double discount;
    private String image_path;
    private String name;
    private String stock;
    private List<WeightCategoryDTO> weight_category;

    public ProductDTO() {
    }

    public ProductDTO(String category, double discount, String image_path, String name, String stock, List<WeightCategoryDTO> weight_category, String id) {
        this.category = category;
        this.discount = discount;
        this.image_path = image_path;
        this.name = name;
        this.stock = stock;
        this.weight_category = weight_category;
        this.id = id;
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
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

    public List<WeightCategoryDTO> getWeight_category() {
        return weight_category;
    }

    public void setWeight_category(List<WeightCategoryDTO> weight_category) {
        this.weight_category = weight_category;
    }
}
