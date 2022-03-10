package com.example.demo.src.cart.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetMenuOptionRes {
    @NonNull private int menuId;
    private List<MenuOptionCategory> menuOptionCategoryList;

}