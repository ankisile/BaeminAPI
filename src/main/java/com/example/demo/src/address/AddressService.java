package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackFor = BaseException.class)
public class AddressService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AddressDao addressDao;
    private final AddressProvider addressProvider;
    private final JwtService jwtService;

    @Autowired
    public AddressService(AddressDao addressDao, AddressProvider addressProvider, JwtService jwtService) {
        this.addressDao = addressDao;
        this.addressProvider = addressProvider;
        this.jwtService = jwtService;
    }

    public void createUserAddress(PostAddressReq postAddressReq, int userId) throws BaseException {

        if(postAddressReq.getAddress() == null){
            throw new BaseException(INVALID_ADDRESS);
        }
        try {
//            if(postAddressReq.getAddressType()==1){
//                addressDao.modifyAddressTypeHouse(userId);
//            }
//            if(postAddressReq.getAddressType()==2){
//                addressDao.modifyAddressTypeCompany(userId);
//            }
            addressDao.createUserAddress(new Address(userId, postAddressReq.getAddress(),postAddressReq.getDetails(),
                        postAddressReq.getAddressType(), postAddressReq.getNickName()));
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyAddressActiveYn(int userId, int addressId) throws BaseException {
        try{
            addressDao.modifyAddressActiveY(userId, addressId);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}