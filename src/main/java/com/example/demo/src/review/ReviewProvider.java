package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final JwtService jwtService;

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }


    public int checkOrderInfoId(int userId, int orderInfoId) throws BaseException {
        try {
            return reviewDao.checkOrderInfoId(userId, orderInfoId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkExistReview(int userId, int orderInfoId) throws BaseException {
        try {
            return reviewDao.checkExistReview(userId, orderInfoId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getStoreId(int userId, int orderInfoId) throws BaseException {
        try {
            return reviewDao.getStoreId(userId, orderInfoId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkOrderMenuId(int orderMenuId, PostReviewReq postReviewReq) throws BaseException {
        try {
            return reviewDao.checkOrderMenuId(orderMenuId, postReviewReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewId(int userId) throws BaseException {
        try {
            return reviewDao.checkReviewId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getUserReviewCount(int userId) throws BaseException {
        try {
            return reviewDao.getUserReviewCount(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Review> getUserReviews(int userId) throws BaseException {
        try {
            List<Review> reviewList = reviewDao.getStoreReviews(userId);
            for (Review review : reviewList) {
                review.setReviewImgList(reviewDao.getReviewImgs(review.getReviewId()));
                review.setReviewMenuList(reviewDao.getReviewMenus(review.getReviewId()));
            }
            return reviewList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    public int checkReviewIdByReviewId(int userId, int reviewId) throws BaseException {
        try {
            return reviewDao.checkReviewIdByReviewId(userId, reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
//
//    public int checkReviewIdByUserId(int userId) throws BaseException {
//        try {
//            return reviewDao.checkReviewIdByUserId(userId);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

    public int checkReviewRecommendByReviewId(int reviewId) throws BaseException {
        try {
            return reviewDao.checkReviewRecommendByReviewId(reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewImgByReviewId(int reviewId) throws BaseException {
        try {
            return reviewDao.checkReviewImgByReviewId(reviewId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




    public int checkStoreId(int storeId) throws BaseException {
        try {
            return reviewDao.checkStoreId(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}