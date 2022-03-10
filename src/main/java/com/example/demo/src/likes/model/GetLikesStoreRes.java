package com.example.demo.src.likes.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetLikesStoreRes {
    private String totalCount;
    private List<Store> storeInfoList;
}