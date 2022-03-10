package com.example.demo.src.likes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.likes.model.*;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexInteger;

@RestController
@RequestMapping("/app/likes")
public class LikesController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LikesProvider likesProvider;
    private final LikesService likesService;
    private final JwtService jwtService;

    @Autowired
    public LikesController(LikesService likesService,LikesProvider likesProvider, JwtService jwtService) {
        this.likesService = likesService;
        this.likesProvider = likesProvider;
        this.jwtService = jwtService;
    }

    /**
     * 찜 활성 API
     * [POST] /likes
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> pushLikes(@RequestBody PostLikesReq postLikesReq) throws BaseException {
        if(postLikesReq.getStoreId() == null){
            return new BaseResponse<>(POST_LIKES_EMPTY_STORE_ID);
        }
        if(likesProvider.checkStore(postLikesReq.getStoreId()) == 0){
            return new BaseResponse<>(INVALID_STORE_ID);
        }
        try{
            // jwt 에서 userId 추출
            int userIdByJwt = jwtService.getUserId();

            if(likesProvider.checkLikes(userIdByJwt, postLikesReq.getStoreId()) == 0) {
                likesService.createLikes(userIdByJwt, postLikesReq.getStoreId());

            }
            else{
                likesService.updateLikesStatus(userIdByJwt, postLikesReq.getStoreId());

            }
            return new BaseResponse<>("success");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 즐겨찾기 매장 조회 API
     * [GET] /likes
     *
     *
     * @return BaseResponse<GetLikesStoreRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetLikesStoreRes> getLikesStoreResBaseResponse() {
        try {
            // jwt 에서 userId 추출.
            int userIdByJwt = jwtService.getUserId();

            if(likesProvider.checkLikesByUserId(userIdByJwt) == 0){
                return new BaseResponse<>(EMPTY_LIKES);
            }

            String totalCount = null;
            List<Store> storeList = null;

            storeList = likesProvider.getStores(userIdByJwt);
            totalCount = likesProvider.getTotalLikesCount(userIdByJwt);

            GetLikesStoreRes getLikesStoreRes = new GetLikesStoreRes(totalCount, storeList);
            return new BaseResponse<>(getLikesStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}