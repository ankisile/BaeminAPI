package com.example.demo.src.store.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Review {
    @NonNull private int reviewId;
    @NonNull private String reviewWriter;
    @NonNull private String reviewDesc;
    @NonNull private int reviewRate;
    @NonNull private String createdAt;
    private List<ReviewImg> reviewImgList;
    private List<ReviewMenu> reviewMenuList;
}