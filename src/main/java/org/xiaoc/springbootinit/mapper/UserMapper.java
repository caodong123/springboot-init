package org.xiaoc.springbootinit.mapper;

import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.xiaoc.springbootinit.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author CAODONG
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-10-23 23:03:37
* @Entity org.xiaoc.springbootinit.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {


}




