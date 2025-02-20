package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.util.List;

public class ProductDTO implements Serializable {

    private String id; // doc id
    private String category;
    private String name;
    private String stock;
    private double discount;
    private List<WeightCategoryDTO> weight_category;
    private String image_path;
    private String referencePath;

    public ProductDTO() {
    }

    public ProductDTO(String id, String category, String name, String stock, double discount, List<WeightCategoryDTO> weight_category, String image_path, String referencePath) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.stock = stock;
        this.discount = discount;
        this.weight_category = weight_category;
        this.image_path = image_path;
        this.referencePath = referencePath;
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

    public List<WeightCategoryDTO> getWeight_category() {
        return weight_category;
    }

    public void setWeight_category(List<WeightCategoryDTO> weight_category) {
        this.weight_category = weight_category;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getReferencePath() {
        return referencePath;
    }

    public void setReferencePath(String referencePath) {
        this.referencePath = referencePath;
    }
}
