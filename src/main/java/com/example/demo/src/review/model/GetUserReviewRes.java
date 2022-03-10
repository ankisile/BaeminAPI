package com.example.demo.src.review.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetUserReviewRes {
    @NonNull private String totalCount;
    private List<Review> reviewList;
}
