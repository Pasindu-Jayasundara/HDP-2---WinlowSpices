package com.example.winloadmin.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {

    private String id;
    private String photo_url;
    private String name;
    private String email;

    public UserDTO() {
    }

    public UserDTO(String id, String photo_url, String name, String email) {
        this.id = id;
        this.photo_url = photo_url;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
