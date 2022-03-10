package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class StoreProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final JwtService jwtService;

    @Autowired
    public StoreProvider(StoreDao storeDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }



    public List<GetStoreRes> getStores(int categoryId) throws BaseException {
        try {
            List<GetStoreRes> storeResList = storeDao.getStores(categoryId);
            return storeResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetStoreImgRes> getStoreImages(int storeId) throws BaseException {
        try {
            return storeDao.getStoreImages(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkStoreId(int storeId) throws BaseException {
        try {
            return storeDao.checkStoreId(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetStoreInfoRes getStoreInfos(int storeId) throws BaseException {
        try{
            GetStoreInfoRes getStoreInfoRes = storeDao.getStoreInfos(storeId);
            return getStoreInfoRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<MenuCategory> getMenuCategories(int storeId) throws BaseException {
        try {
            List<MenuCategory> menuCategoryList = storeDao.getMenuCategories(storeId);
            for (MenuCategory menuCategory : menuCategoryList) {
                menuCategory.setMenuList(storeDao.getMenus(menuCategory.getMenuCategoryId()));
            }
            return menuCategoryList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




    public List<Review> getStoreReviews(int storeId) throws BaseException {
        try {
            List<Review> reviewList = storeDao.getStoreReviews(storeId);
            for (Review review : reviewList) {
                review.setReviewImgList(storeDao.getReviewImgs(review.getReviewId()));
                review.setReviewMenuList(storeDao.getReviewMenus(review.getReviewId()));
            }
            return reviewList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}