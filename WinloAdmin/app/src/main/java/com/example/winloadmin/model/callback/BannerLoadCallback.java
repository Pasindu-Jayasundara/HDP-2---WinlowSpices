package com.example.winloadmin.model.callback;

import com.example.winloadmin.dto.BannerDTO;

import java.util.List;

public interface BannerLoadCallback {

    void onBannerLoad(boolean isSuccess, List<BannerDTO> bannerDTOList);
}
