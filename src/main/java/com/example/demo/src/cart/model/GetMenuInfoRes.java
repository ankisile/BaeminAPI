package com.example.demo.src.cart.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMenuInfoRes {
//    @NonNull private int menuId;
//    @NonNull private String menuName;
//    @NonNull private String menuDesc;
//    @NonNull private String menuPrice;
//    @NonNull private String menuImg;
//    private List<MenuOptionCategory> menuOptionCategoryList;

    private int menuId;
    private String menuName;
    private String menuDesc;
    private int menuPrice;
    private String menuImg;
}
