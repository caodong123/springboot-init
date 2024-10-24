package org.xiaoc.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.model.dto.user.UserRegisterRequest;
import org.xiaoc.springbootinit.model.entity.User;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.service.UserService;
import org.xiaoc.springbootinit.mapper.UserMapper;
import org.springframework.stereotype.Service;

import static org.xiaoc.springbootinit.constant.UserConstant.SALT;
import static org.xiaoc.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author CAODONG
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-23 23:03:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Override
    public LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号小于4位");
        ThrowUtils.throwIf(userPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码小于6位");
        // 2. 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        // 3. 查询
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptPassword);
        User user = this.getOne(wrapper);
        // 4 校验
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");
        // 5. 存在的话 顺利登录 保存登录信息到session
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        // 6. 获取VO并返回
        return getLoginUserVO(user);
    }

    /**
     * 注册后返回 注册成功的用户id
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long register(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR,"账号长度小于4位");
        ThrowUtils.throwIf(userPassword.length() < 6, ErrorCode.PARAMS_ERROR,"密码长度小于6位");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR,"两次密码不一致");
        // 2. 检查是否存在账号
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userAccount);
        long count = this.count(wrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.SYSTEM_ERROR,"账号已存在");
        // 3. 数据加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        // 5. 加入数据库
        boolean save = this.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR,"注册失败");
        // 6. 返回id
        return user.getId();
    }


    /**
     * 获取登录用户LoginUserVO
     * @param user
     * @return
     */
    private LoginUserVO getLoginUserVO(User user) {
        if(user ==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user,loginUserVO);
        return loginUserVO;
    }
}




