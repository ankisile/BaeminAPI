package com.example.demo.src.review.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Review {
    @NonNull private int reviewId;
    @NonNull private int storeId;
    @NonNull private String storeName;
    @NonNull private int reviewRate;
    @NonNull private String reviewDesc;
    @NonNull private String updatedAt;
    private List<ReviewImg> reviewImgList;
    private List<ReviewMenu> reviewMenuList;
}