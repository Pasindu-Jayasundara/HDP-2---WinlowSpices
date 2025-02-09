package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.util.List;

public class CartDTO implements Serializable {

    private String referencePath;
    private List<CartWeightCategoryDTO> CartWeightCategoryDTOList;

    public CartDTO() {
    }

    public CartDTO(String referencePath, List<CartWeightCategoryDTO> CartWeightCategoryDTOList) {
        this.referencePath = referencePath;
        this.CartWeightCategoryDTOList = CartWeightCategoryDTOList;
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
