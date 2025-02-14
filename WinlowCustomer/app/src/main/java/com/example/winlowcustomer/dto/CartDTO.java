package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CartDTO implements Serializable {

    private String referencePath; //

    private ProductDTO product;
    boolean isChecked;
    private String weight;

//    private String name;
    private List<CartWeightCategoryDTO> CartWeightCategoryDTOList;

    private Map<String, Object> cartDTOMap;

    public CartDTO() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public CartDTO(String referencePath, Map<String, Object> cartDTOMap, ProductDTO productDTO, List<CartWeightCategoryDTO> CartWeightCategoryDTOList, String weight) {
        this.referencePath = referencePath;
//        this.fieldPath = fieldPath;
        this.product = productDTO;
        this.cartDTOMap = cartDTOMap;
//        this.name = name;
        this.CartWeightCategoryDTOList = CartWeightCategoryDTOList;
        this.weight = weight;

    }

//    public String getFieldPath() {
//        return fieldPath;
//    }
//
//    public void setFieldPath(String fieldPath) {
//        this.fieldPath = fieldPath;
//    }

    public Map<String, Object> getCartDTOMap() {
        return cartDTOMap;
    }

    public void setCartDTOMap(Map<String, Object> cartDTOMap) {
        this.cartDTOMap = cartDTOMap;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getReferencePath() {
        return referencePath;
    }

    public void setReferencePath(String referencePath) {
        this.referencePath = referencePath;
    }

    public List<CartWeightCategoryDTO> getCartWeightCategoryDTOList() {
        return CartWeightCategoryDTOList;
    }

    public void setCartWeightCategoryDTOList(List<CartWeightCategoryDTO> cartWeightCategoryDTOList) {
        CartWeightCategoryDTOList = cartWeightCategoryDTOList;
    }
}
