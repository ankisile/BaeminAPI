package com.example.demo.src.likes;

import com.example.demo.config.BaseException;
import com.example.demo.src.likes.model.Store;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class LikesProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LikesDao likesDao;
    private final JwtService jwtService;

    @Autowired
    public LikesProvider(LikesDao likesDao, JwtService jwtService) {
        this.likesDao = likesDao;
        this.jwtService = jwtService;
    }

    public int checkStore(int storeId) throws BaseException {
        try {
            return likesDao.checkStore(storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkLikes(int userId, int storeId) throws BaseException {
        try {
            return likesDao.checkLikes(userId, storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkLikesByUserId(int userId) throws BaseException {
        try {
            return likesDao.checkLikesByUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkLikesStatus(int userId, int storeId) throws BaseException {
        try {
            return likesDao.checkLikesStatus(userId, storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Store> getStores(int userId) throws BaseException {
        try {
            List<Store> storeList = likesDao.getStores(userId);
            return storeList;
        } catch (Exception exception) {
            throw new BaseException(POST_USERS_INVALID_TYPE);
        }
    }

    public String getTotalLikesCount(int userId) throws BaseException {
        try {
            return likesDao.getTotalLikesCount(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}