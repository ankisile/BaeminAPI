package com.example.demo.src.cart;

import com.example.demo.config.BaseException;
import com.example.demo.src.cart.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackFor = BaseException.class)
public class CartService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CartDao cartDao;
    private final CartProvider cartProvider;
    private final JwtService jwtService;

    @Autowired
    public CartService(CartDao cartDao, CartProvider cartProvider, JwtService jwtService) {
        this.cartDao = cartDao;
        this.cartProvider = cartProvider;
        this.jwtService = jwtService;
    }

    public void addToCart(int userId, PostCartReq postCartReq) throws BaseException {
        try{
            // 카트가 비어있을 때
            if (cartProvider.checkCartId(userId) == 0) {

                int cartId = cartDao.makeCart(userId); // 새로운 카트 생성

                // 새로운 카트 메뉴 생성
                cartDao.makeCartMenu(userId,cartId, postCartReq);

            }
            // 카트가 존재할 때
            else {
                // 카트 메뉴가 존재할 때, 기존 카트 메뉴에 count 만큼 증가
                if (cartProvider.checkCartMenuId(userId, postCartReq.getMenuId()) == 1) {
                    cartDao.modifyCartMenuCount(userId, postCartReq);
                }
                // 카트 메뉴가 존재하지 않을 때, 새로운 카트 메뉴 생성
                else {
                    int cartId = cartProvider.getCartId(userId);
                    cartDao.makeCartMenu(userId,cartId, postCartReq);
                }
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCart(int userId) throws BaseException{
        try {
            cartDao.deleteCartMenu(userId);
            cartDao.deleteCart(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void deleteCartMenu(int userId, int cartMenuId) throws BaseException {
        try{
            cartDao.deleteCartMenuByCartMenuId(userId, cartMenuId);
            if(cartProvider.checkCartMenuIdByUserId(userId) == 0){
                cartDao.deleteCart(userId);
            }
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    public void createOrder(PostOrderReq postOrderReq, int userId) throws BaseException{
        try{
            int storeId = cartProvider.getStore(userId);
            int totalPrice = cartProvider.getTotalPriceIntValue(userId);

            String address = cartProvider.getUserAddress(userId);

            List<CartMenus> cartMenusList = cartProvider.cartMenusList(userId);

            int orderId = cartDao.makeOrderInfo(userId, storeId, address, totalPrice, postOrderReq);

            for(CartMenus cartMenu : cartMenusList){
                cartDao.makeOrderInfoPrice(orderId, cartMenu.getMenuName(), cartMenu.getCount(), cartMenu.getPrice());
            }
            cartDao.deleteCartMenu(userId);
            cartDao.deleteCart(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}