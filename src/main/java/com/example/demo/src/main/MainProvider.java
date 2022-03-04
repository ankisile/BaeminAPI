package com.example.demo.src.main;

import com.example.demo.config.BaseException;
import com.example.demo.src.main.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MainProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MainDao mainDao;
    private final JwtService jwtService;

    @Autowired
    public MainProvider(MainDao mainDao, JwtService jwtService) {
        this.mainDao = mainDao;
        this.jwtService = jwtService;
    }

    public String getMainAddress(int userId) throws BaseException {
        try {
            return mainDao.getMainAddress(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public List<GetStoreCategoryRes> getStoreCategoryRes() throws BaseException {
        try {
            return mainDao.getStoreCategoryRes();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}