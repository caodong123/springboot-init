package org.xiaoc.springbootinit.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.xiaoc.springbootinit.common.BaseResponse;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.common.ResultUtils;
import org.xiaoc.springbootinit.exception.BusinessException;
import org.xiaoc.springbootinit.model.dto.user.UserLoginRequest;
import org.xiaoc.springbootinit.model.dto.user.UserRegisterRequest;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        long result = userService.register(userAccount,userPassword,checkPassword);
        return ResultUtils.success(result);
    }


    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号和密码不能为空");
        }
        LoginUserVO loginUser = userService.login(userAccount, userPassword,request);
        return ResultUtils.success(loginUser);

    }




}
