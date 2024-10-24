package org.xiaoc.springbootinit.service;

import jakarta.servlet.http.HttpServletRequest;
import org.xiaoc.springbootinit.model.dto.user.UserRegisterRequest;
import org.xiaoc.springbootinit.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;

/**
* @author CAODONG
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-10-23 23:03:37
*/
public interface UserService extends IService<User> {

    LoginUserVO login(String userAccount, String userPassword, HttpServletRequest request);


    long register(String userAccount, String userPassword, String checkPassword);
}
