package com.example.winloadmin.dto;

import java.io.Serializable;

public class BannerDTO implements Serializable {

    private String path;

    public BannerDTO() {
    }

    public BannerDTO(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
