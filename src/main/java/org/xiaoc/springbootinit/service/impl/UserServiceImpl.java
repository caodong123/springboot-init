package org.xiaoc.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.constant.PageConstant;
import org.xiaoc.springbootinit.constant.UserConstant;
import org.xiaoc.springbootinit.exception.BusinessException;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.model.dto.user.UserQueryRequest;
import org.xiaoc.springbootinit.model.entity.User;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.model.vo.UserVO;
import org.xiaoc.springbootinit.service.UserService;
import org.xiaoc.springbootinit.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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


    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
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
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public boolean logout(HttpServletRequest request) {
        if(request.getSession().getAttribute(USER_LOGIN_STATE) == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取当前登录的用户
     * @param request
     * @return
     */
    @Override
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        if(request.getSession().getAttribute(USER_LOGIN_STATE) == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        // 获取登录态中记录的用户
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        // 防止获得脏数据，重新在数据库中查询
        user = this.getById(user.getId());
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        return getLoginUserVO(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return this.removeById(id);
    }

    /**
     * 构造查询的wrapper
     * @param userQueryRequest
     * @return
     */
    @Override
    public Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortFiled = userQueryRequest.getSortFiled();
        String sortOrder = userQueryRequest.getSortOrder();
        // 构建条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        // 精确查询  id/userAccount/用户类型
        wrapper.eq(id != null, "id", id);
        wrapper.eq(StringUtils.isNotBlank(userAccount),"userAccount",userAccount);
        wrapper.eq(StringUtils.isNotBlank(userRole),"userRole",userRole);
        // 模糊查询  userName/userProfile
        wrapper.like(StringUtils.isNotBlank(userName),"userName",userName);
        wrapper.like(StringUtils.isNotBlank(userProfile),"userProfile",userProfile);
        // 排序
        wrapper.orderBy(StringUtils.isNotBlank(sortFiled),StringUtils.equals(sortOrder,PageConstant.SORT_ORDER_ASC),sortFiled);

        return wrapper;
    }

    /**
     * 获取用户脱敏后的信息
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVo(List<User> userList) {
        if(userList == null || userList.size() <= 0){
            return new ArrayList<>();
        }
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVo = new UserVO();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }).toList();
        return userVOList;
    }

    /**
     * 根据id封禁用户
     * @param id
     * @return
     */
    @Override
    public boolean banUser(Long id) {
        boolean result = update(Wrappers.lambdaUpdate(User.class).eq(User::getId, id).set(User::getUserRole, UserConstant.BAN));
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR,"封禁用户失败");
        return result;
    }

    /**
     * 批量删除用户
     * @param userIdList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelteUser(List<Long> userIdList) {
        //校验数据
        ThrowUtils.throwIf(CollUtil.isEmpty(userIdList), ErrorCode.PARAMS_ERROR);
        //批量删除
        for (Long id : userIdList) {
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return true;
    }


    /**
     *    user -> loginUserVO
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




