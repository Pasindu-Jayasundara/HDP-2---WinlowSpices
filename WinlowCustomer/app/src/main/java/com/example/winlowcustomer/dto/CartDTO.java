package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CartDTO implements Serializable {

    private String referencePath; //
    private String name;
    private List<CartWeightCategoryDTO> CartWeightCategoryDTOList;

    private Map<String, Object> cartDTOMap;

    public CartDTO() {
    }

    public CartDTO(String referencePath,Map<String, Object> cartDTOMap, String name, List<CartWeightCategoryDTO> CartWeightCategoryDTOList) {
        this.referencePath = referencePath;
//        this.fieldPath = fieldPath;
        this.cartDTOMap = cartDTOMap;
        this.name = name;
        this.CartWeightCategoryDTOList = CartWeightCategoryDTOList;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
