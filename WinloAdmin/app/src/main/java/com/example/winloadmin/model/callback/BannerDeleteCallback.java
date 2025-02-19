package com.example.winloadmin.model.callback;

import com.example.winloadmin.dto.BannerDTO;

public interface BannerDeleteCallback {

    void onDelete(boolean isDeleted,String path);
}
