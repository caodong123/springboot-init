package org.xiaoc.springbootinit.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.xiaoc.springbootinit.annotation.AuthCheck;
import org.xiaoc.springbootinit.common.BaseResponse;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.common.ResultUtils;
import org.xiaoc.springbootinit.exception.BusinessException;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.model.dto.user.*;
import org.xiaoc.springbootinit.model.entity.User;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.model.vo.UserVO;
import org.xiaoc.springbootinit.service.UserService;

import java.util.List;

import static org.xiaoc.springbootinit.constant.UserConstant.ADMIN;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
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


    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
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

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request){
        ThrowUtils.throwIf(request==null,ErrorCode.PARAMS_ERROR);
        boolean result = userService.logout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前的登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request){
        ThrowUtils.throwIf(request==null,ErrorCode.PARAMS_ERROR);
        LoginUserVO loginUser = userService.getLoginUser(request);
        return ResultUtils.success(loginUser);
    }

    /**
     * 用户删除
     * @param userDeleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(Role = ADMIN)
    public BaseResponse<Boolean> userDelete(@RequestBody UserDeleteRequest userDeleteRequest){
        ThrowUtils.throwIf(userDeleteRequest==null || userDeleteRequest.getId() < 0,ErrorCode.PARAMS_ERROR);
        boolean result = userService.deleteUser(userDeleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新用户信息
     * @param UserUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateRequest UserUpdateRequest, HttpServletRequest request){
        //参数校验
        ThrowUtils.throwIf(UserUpdateRequest==null,ErrorCode.PARAMS_ERROR);
        LoginUserVO loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser==null,ErrorCode.NOT_LOGIN_ERROR);
        //构造user
        User user = new User();
        BeanUtils.copyProperties(UserUpdateRequest, user);
        //传入id
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR,"更新失败");
        return ResultUtils.success(result);
    }


    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest,HttpServletRequest request){
        ThrowUtils.throwIf(userQueryRequest==null,ErrorCode.PARAMS_ERROR);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        //限制一次获取的数量
        ThrowUtils.throwIf( pageSize > 200,ErrorCode.PARAMS_ERROR);
        //获取查询wrapper
        Wrapper<User> wrapper = userService.getQueryWrapper(userQueryRequest);
        IPage<User> userPage = userService.page(new Page<User>(current, pageSize), wrapper);
        //获取数据
        Page<UserVO> userVoPage = new Page<>(current,pageSize,userPage.getTotal());
        // 获取脱敏后的数据
        List<UserVO> userVOList = userService.getUserVo(userPage.getRecords());
        userVoPage.setRecords(userVOList);
        return ResultUtils.success(userVoPage);
    }

    /**
     * 用户封禁
     * @param userBanRequest
     * @return
     */
    @PostMapping("/ban")
    @AuthCheck(Role = ADMIN)
    public BaseResponse<Boolean> userBan(@RequestBody UserBanRequest userBanRequest){
        ThrowUtils.throwIf(userBanRequest==null || userBanRequest.getId() < 0,ErrorCode.PARAMS_ERROR);
        boolean result = userService.banUser(userBanRequest.getId());
        return ResultUtils.success(result);
    }


}
