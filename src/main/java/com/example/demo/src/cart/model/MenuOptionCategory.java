package com.example.demo.src.cart.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class MenuOptionCategory {
    @NonNull private int menuOptionCategoryId;
    @NonNull private String menuOptionCategoryName;
    private List<MenuOption> menuOptionList;
}