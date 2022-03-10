package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {

        String pwd = postUserReq.getPassword();
        String email = postUserReq.getEmail();
        String nickName = postUserReq.getUserNickName();
        String phone = postUserReq.getPhoneNumber();

        if(email == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(pwd == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(nickName == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if(phone == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }


        if(!isRegexEmail(email)){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(userProvider.checkEmail(email) == 1){         //이메일 있는지 확인
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        }
        if(!((isRegexEngNumSpecialCharCombinationPwd(pwd) && !isRegexEngNumCombinationPwd(pwd) && !isRegexNumSpecialCharCombinationPwd(pwd) && !isRegexEngSpecialCharCombinationPwd(pwd))
                || (!isRegexEngNumSpecialCharCombinationPwd(pwd) && isRegexEngNumCombinationPwd(pwd) && !isRegexNumSpecialCharCombinationPwd(pwd) && !isRegexEngSpecialCharCombinationPwd(pwd))
                || (!isRegexEngNumSpecialCharCombinationPwd(pwd) && !isRegexEngNumCombinationPwd(pwd) && isRegexNumSpecialCharCombinationPwd(pwd) && !isRegexEngSpecialCharCombinationPwd(pwd))
                || (!isRegexEngNumSpecialCharCombinationPwd(pwd) && !isRegexEngNumCombinationPwd(pwd) && !isRegexNumSpecialCharCombinationPwd(pwd) && isRegexEngSpecialCharCombinationPwd(pwd)))
        ){
            return new BaseResponse<>(POST_USERS_PASSWORD_COMBINATION);
        }

        if(isContainEmailId(pwd, email)){
            return new BaseResponse<>(POST_USERS_PASSWORD_CONTAIN_ID);
        }
        if(!isRegexPhone(phone)){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }


        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

        /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) throws BaseException{

        String pwd = postLoginReq.getPassword();
        String email = postLoginReq.getEmail();

        if(email == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(pwd == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(userProvider.checkEmail(postLoginReq.getEmail()) == 0){
            return new BaseResponse<>(FAILED_TO_LOGIN);
        }

        try{
            PostLoginRes postLoginRes = userProvider.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


//
//    /**
//     * 회원 1명 조회 API
//     * [GET] /users/:userIdx
//     * @return BaseResponse<GetUserRes>
//     */
//    // Path-variable
//    @ResponseBody
//    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
//    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
//        // Get Users
//        try{
//            GetUserRes getUserRes = userProvider.getUser(userIdx);
//            return new BaseResponse<>(getUserRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }



//
//    /**
//     * 유저정보변경 API
//     * [PATCH] /users/:userIdx
//     * @return BaseResponse<String>
//     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}")
//    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
//        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getUserName());
//            userService.modifyUserName(patchUserReq);
//
//            String result = "";
//        return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }


}
