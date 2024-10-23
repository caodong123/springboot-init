package org.xiaoc.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.xiaoc.springbootinit.model.entity.User;
import org.xiaoc.springbootinit.service.UserService;
import org.xiaoc.springbootinit.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author CAODONG
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-23 23:03:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




