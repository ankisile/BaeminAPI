package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Menu {
    private int menuId;
    private String menuName;
    private int menuPrice;
    private String menuImg;
    private String menuDesc;
    private String popularMenu;
    private String available;

}