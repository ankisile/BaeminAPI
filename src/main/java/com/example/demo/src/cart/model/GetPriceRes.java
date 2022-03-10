package com.example.demo.src.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPriceRes {
    private int orderPrice;
    private int deliveryFee;
    private String totalPrice;
}