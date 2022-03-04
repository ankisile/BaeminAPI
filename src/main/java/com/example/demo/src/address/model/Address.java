package com.example.demo.src.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Address {
    private int userId;
    private String address;
    private String details;
    private String addressType;
    private String nickName;
}