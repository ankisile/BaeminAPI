package com.example.demo.src.likes.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Store {
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