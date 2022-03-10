package com.example.demo.src.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCartMenuRes {
    private int cartMenuId;
        private String menuImg;
    private String cartMenuName;
    private int cartMenuCount;
    private String totalPrice;
}