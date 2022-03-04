package com.example.demo.src.store.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreRes {
    private int storeId;
    private String storeName;
    private int minPrice;
    private String deliveryTime;
    private String deliveryTip;
    private String representImg;
    private float rate;
    private int reviewCount;
    private String coupon;
    private String togo;
    private String popularFood;
    private String available;
    private String newStore;




}