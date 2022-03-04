package com.example.demo.src.main;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.main.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DELETED_USER;

@RestController
@RequestMapping("/app")
public class MainController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MainService mainService;
    private final MainProvider mainProvider;
    private final JwtService jwtService;

    @Autowired
    public MainController(MainService mainService, MainProvider mainProvider, JwtService jwtService) {
        this.mainService = mainService;
        this.mainProvider = mainProvider;
        this.jwtService = jwtService;
    }

    /**
     * 메인 화면 - 주소 표시 API
     * [GET] /users/addresses
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/users/addresses")
    public BaseResponse<String> getMainAddress() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            return new BaseResponse<>(mainProvider.getMainAddress(userIdByJwt));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 메인 화면 - 매장 카테고리 API
     * [GET] /categories
     * @return BaseResponse<List<GetStoreCategoryRes>>
     */
    @ResponseBody
    @GetMapping("/categories")
    public BaseResponse<List<GetStoreCategoryRes>> getStoreCategoryRes() {
        try {
            return new BaseResponse<>(mainProvider.getStoreCategoryRes());
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
