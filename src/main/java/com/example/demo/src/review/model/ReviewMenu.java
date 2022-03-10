package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewMenu {
    private int menuId;
    private String menuName;
    private String recommend;
    private String recommendDesc;

}