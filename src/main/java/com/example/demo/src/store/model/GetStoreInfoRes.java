package com.example.demo.src.store.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreInfoRes {
    private int storeId;
    private String storeName;
    private String phoneNumber;
    private int minPrice;
    private double starRate;
    private String reviewCount;
    private String deliveryTime;
    private String deliveryTip;
    private String available;
    private String coupon;
    private String likes;

}