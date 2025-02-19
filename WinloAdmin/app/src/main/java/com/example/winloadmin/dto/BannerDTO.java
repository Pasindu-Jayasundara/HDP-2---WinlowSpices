package com.example.winloadmin.dto;

import java.io.Serializable;

public class BannerDTO implements Serializable {

    private String id;
    private String path;

    public BannerDTO(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BannerDTO() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
