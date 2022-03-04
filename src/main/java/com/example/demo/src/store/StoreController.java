package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app/stores")
public class StoreController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreService storeService;
    private final StoreProvider storeProvider;
    private final JwtService jwtService;

    @Autowired
    public StoreController(StoreService storeService, StoreProvider storeProvider, JwtService jwtService) {
        this.storeService = storeService;
        this.storeProvider = storeProvider;
        this.jwtService = jwtService;
    }

    /**
     * 매장 선택 화면 - 기본 API
     * [GET] /stores
     *
     * 매장 선택 화면
     * [GET] /stores?categoryId=
     *
     * @return BaseResponse<List<GetStoreRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetStoreRes>> getStores(@RequestParam(required = false) String categoryId) {
        try {

            if(!isRegexInteger(categoryId)){
                return new BaseResponse<>(INVAILD_QUERY_PARAMS);
            }
            int id = Integer.parseInt(categoryId);
            List<GetStoreRes> stores = storeProvider.getStores(id);
            return new BaseResponse<>(stores);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 특정 매장 화면 - 이미지 API
     * [GET] /stores/:storeId/images
     * @return BaseResponse<List<StoreImage>>
     */
    @ResponseBody
    @GetMapping("/{storeId}/images")
    public BaseResponse<List<GetStoreImgRes>> getStoreImages(@PathVariable(required = false) String storeId) {
        if(storeId == null){
            return new BaseResponse<>(EMPTY_PATH_VARIABLE);
        }
        try {
            if(!isRegexInteger(storeId)){
                return new BaseResponse<>(INVAILD_PATH_VARIABLE);
            }
            int id = Integer.parseInt(storeId);

            return new BaseResponse<>(storeProvider.getStoreImages(id));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}