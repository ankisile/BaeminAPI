package com.example.demo.src.likes;

import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackFor = BaseException.class)
public class LikesService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LikesProvider likesProvider;
    private final LikesDao likesDao;
    private final JwtService jwtService;

    @Autowired
    public LikesService(LikesDao likesDao, LikesProvider likesProvider, JwtService jwtService) {
        this.likesDao = likesDao;
        this.likesProvider = likesProvider;
        this.jwtService = jwtService;
    }

    public void createLikes(int userId, int storeId) throws BaseException {
        try {
            likesDao.createLikes(userId, storeId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateLikesStatus(int userId, int storeId) throws BaseException {
        try {
            if(likesProvider.checkLikesStatus(userId, storeId)==0){
                likesDao.activeLikesStatus(userId, storeId);
            }
            else if(likesProvider.checkLikesStatus(userId, storeId)==1){
                likesDao.inactiveLikesStatus(userId, storeId);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}