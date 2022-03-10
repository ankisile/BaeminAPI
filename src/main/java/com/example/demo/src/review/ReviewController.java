package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app/reviews")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewService reviewService;
    private final ReviewProvider reviewProvider;
    private final JwtService jwtService;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewProvider reviewProvider, JwtService jwtService) {
        this.reviewService = reviewService;
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
    }

    /**
     * 리뷰 작성 API
     * [POST] /reviews
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> postReviews(@RequestBody PostReviewReq postReviewReq) {
        if(postReviewReq.getOrderId() == null){
            return new BaseResponse<>(POST_REVIEWS_EMPTY_ORDER_INFO_ID);
        }

        if(!(postReviewReq.getReviewRate() >= 1 && postReviewReq.getReviewRate() <= 5)){
            return new BaseResponse<>(POST_REVIEWS_INVALID_SCORE);
        }

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(reviewProvider.checkOrderInfoId(userIdByJwt, postReviewReq.getOrderId()) == 0){
                return new BaseResponse<>(INVALID_ORDER_INFO_ID);
            }

            if(reviewProvider.checkExistReview(userIdByJwt, postReviewReq.getOrderId()) == 1){
                return new BaseResponse<>(EXISTS_REVIEW);
            }

            int storeId = reviewProvider.getStoreId(userIdByJwt, postReviewReq.getOrderId());


            if(postReviewReq.getOrderMenu() != null) {
                List<OrderMenu> orderMenuList = postReviewReq.getOrderMenu();
                for (OrderMenu orderMenu : orderMenuList) {
                    if (reviewProvider.checkOrderMenuId(orderMenu.getOrderMenuId(), postReviewReq) == 0) {
                        return new BaseResponse<>(INVALID_ORDER_INFO_MENU_ID);

                    }
                }

            }


            int reviewId = reviewService.createReview(storeId, userIdByJwt, postReviewReq);

            if(postReviewReq.getOrderMenu() != null){
                List<OrderMenu> orderMenuList = postReviewReq.getOrderMenu();
                for(OrderMenu orderMenu : orderMenuList){
                    if(reviewProvider.checkOrderMenuId(orderMenu.getOrderMenuId(), postReviewReq) != 0){
                        if(orderMenu.getRecommend()==1)
                            reviewService.createRecommend(reviewId, orderMenu.getOrderMenuId(), orderMenu.getRecommendDesc());
                    }
                }

            }

            if(postReviewReq.getReviewImgList() != null){
                List<ReviewImg> reviewImgList = postReviewReq.getReviewImgList();
                for(ReviewImg reviewImg : reviewImgList){
                    reviewService.createReviewImage(reviewId, reviewImg.getReviewImg());
                }
            }

            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 내 리뷰 보기 API
     * [GET] /reviews
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetUserReviewRes> getMyReview() {

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            GetUserReviewRes getUserReviewRes = new GetUserReviewRes(reviewProvider.getUserReviewCount(userIdByJwt));
            getUserReviewRes.setReviewList(reviewProvider.getUserReviews(userIdByJwt));

            return new BaseResponse<>(getUserReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 리뷰 삭제 API
     * [DELETE] /reviews/:reviewId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{reviewId}")
    public BaseResponse<String> deleteReview(@PathVariable(required = false) String reviewId) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(reviewProvider.checkReviewId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_REVIEW);
            }
            if(!isRegexInteger(reviewId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(reviewId);
            if(id < 1){
                return new BaseResponse<>(DELETE_REVIEWS_INVALID_REVIEW_ID);
            }
            if(reviewProvider.checkReviewIdByReviewId(userIdByJwt, id) == 0){
                return new BaseResponse<>(INVALID_REVIEW_ID);
            }
            reviewService.deleteReview(userIdByJwt, id);
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}