package org.xiaoc.springbootinit.model.dto.user;

import lombok.Data;
import org.xiaoc.springbootinit.common.PageRequest;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id主键
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色   user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
