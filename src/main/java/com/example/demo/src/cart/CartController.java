package com.example.demo.src.cart;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.cart.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app")
public class CartController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CartService cartService;
    private final CartProvider cartProvider;
    private final JwtService jwtService;

    @Autowired
    public CartController(CartService cartService, CartProvider cartProvider, JwtService jwtService) {
        this.cartService = cartService;
        this.cartProvider = cartProvider;
        this.jwtService = jwtService;
    }

    /**
     * 메뉴 정보 API
     * [GET] /:menuId/infos
     *
     * @return BaseResponse<GetMenuInfoRes>
     */
    @ResponseBody
    @GetMapping("/menus/{menuId}/infos")
    public BaseResponse<GetMenuInfoRes> getMenuInfoRes(@PathVariable(required = false) String menuId) {
        if (menuId == null) {
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if (!isRegexInteger(menuId)) {
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }

            int id = Integer.parseInt(menuId);
            if (cartProvider.checkMenuId(id) == 0) {
                return new BaseResponse<>(INVALID_MENU_ID);
            }
            return new BaseResponse<>(cartProvider.getMenuInfoRes(id));

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 메뉴 정보 - 옵션 API
     * [GET] /:menuId/options
     *
     * @return BaseResponse<GetMenuOptionRes>
     */
    @ResponseBody
    @GetMapping("/menus/{menuId}/options")
    public BaseResponse<GetMenuOptionRes> getMenuOptionRes(@PathVariable(required = false) String menuId) {
        if (menuId == null) {
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if (!isRegexInteger(menuId)) {
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }

            int id = Integer.parseInt(menuId);
            if (cartProvider.checkMenuId(id) == 0) {
                return new BaseResponse<>(INVALID_MENU_ID);
            }

            GetMenuOptionRes getMenuOptionRes = new GetMenuOptionRes(id);
            getMenuOptionRes.setMenuOptionCategoryList(cartProvider.getMenuOptionCategories(id));

            return new BaseResponse<>(getMenuOptionRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 메뉴정보 화면 - 수량에 따른 가격 API
     * [GET] /:menuId/price?count=
     * @return BaseResponse<getMenuPriceRes>
     */
    @ResponseBody
    @GetMapping("/menus/{menuId}/price")
    public BaseResponse<GetMenuPriceRes> getMenuPriceRes(@PathVariable(required = false) String menuId,
                                                         @RequestParam(required = false) String count) {
        if (menuId == null) {
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        if (count == null) {
            return new BaseResponse<>(EMPTY_QUERY_PARAMS);
        }
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if (!isRegexInteger(menuId)) {
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(menuId);
            if (cartProvider.checkMenuId(id) == 0) {
                return new BaseResponse<>(INVALID_MENU_ID);
            }

            if(!isRegexInteger(count)){
                return new BaseResponse<>(INVAILD_QUERY_PARAMS);
            }
            int menuCount = Integer.parseInt(count);
            if(menuCount < 1){
                return new BaseResponse<>(INVALID_MENU_COUNT);
            }
            return new BaseResponse<>(cartProvider.getMenuPriceRes(id, menuCount));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 담기 API
     * [POST] /carts
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/carts")
    public BaseResponse<String> addToCart(@RequestBody PostCartReq postCartReq) {
        if(postCartReq.getMenuId() == null){
            return new BaseResponse<>(POST_CARTS_EMPTY_MENU_ID);
        }
        if(postCartReq.getCount() == null){
            return new BaseResponse<>(POST_CARTS_EMPTY_COUNT);
        }
        if(postCartReq.getMenuId() < 1){
            return new BaseResponse<>(POST_CARTS_INVALID_MENU_ID);
        }
        if(postCartReq.getCount() < 1) {
            return new BaseResponse<>(POST_CARTS_INVALID_COUNT);
        }

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            // 카트가 존재할 때
            if (cartProvider.checkCartId(userIdByJwt) == 1){
                if(cartProvider.checkSameStoreMenu(userIdByJwt, postCartReq.getMenuId()) == 0){ //카트의 메뉴가 집어넣는것과 다르면
                    cartService.deleteCart(userIdByJwt);
                }
            }
            cartService.addToCart(userIdByJwt, postCartReq);
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 메뉴 삭제 API
     * [DELETE] /carts/:cartMenuId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/carts/{cartMenuId}")
    public BaseResponse<String> deleteCartMenu(@PathVariable(required = false) String cartMenuId) {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            if(!isRegexInteger(cartMenuId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(cartMenuId);
            if(id < 1){
                return new BaseResponse<>(DELETE_CARTS_INVALID_CART_MENU_ID);
            }
            if(cartProvider.checkCartMenuIdByCartMenuId(userIdByJwt, id) == 0){
                return new BaseResponse<>(INVALID_CART_MENU_ID);
            }
            cartService.deleteCartMenu(userIdByJwt, id);
            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카트 - 메뉴 목록 API
     * [GET] /carts
     * @return BaseResponse<GetCartMenusRes>
     */
    @ResponseBody
    @GetMapping("/carts")
    public BaseResponse<List<GetCartMenuRes>> getCartMenuResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }

            return new BaseResponse<>(cartProvider.getCartMenuRes(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 주문하기 - 결제 금액 API
     * [GET] /orders/prices
     * @return BaseResponse<GetPriceRes>
     */
    @ResponseBody
    @GetMapping("/orders/prices")
    public BaseResponse<GetPriceRes> getPriceResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }
            GetPriceRes getPriceRes = new GetPriceRes(cartProvider.getOrderPrice(userIdByJwt),
                    cartProvider.getDeliveryFee(userIdByJwt), cartProvider.getTotalPrice(userIdByJwt));
            return new BaseResponse<>(getPriceRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 주문하기 API
     * [POST] /orders
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/orders")
    public BaseResponse<String> postOrders(@RequestBody PostOrderReq postOrderReq) {

        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(cartProvider.checkCartId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_CARTS);
            }

            cartService.createOrder(postOrderReq, userIdByJwt);

            return new BaseResponse<>("success");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

