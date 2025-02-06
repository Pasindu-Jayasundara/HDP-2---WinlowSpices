package com.example.winlowcustomer.dto;

import java.io.Serializable;
import java.util.List;

public class BannerDTO implements Serializable {

    private List<String> imagePathList;

    public BannerDTO() {
    }

    public BannerDTO(List<String> imagePathList) {
        this.imagePathList = imagePathList;
    }

    public List<String> getImagePathList() {
        return imagePathList;
    }

    public void setImagePathList(List<String> imagePathList) {
        this.imagePathList = imagePathList;
    }
}
