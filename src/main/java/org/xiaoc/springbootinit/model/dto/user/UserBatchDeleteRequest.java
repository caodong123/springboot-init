package org.xiaoc.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除用户请求
 */
@Data
public class UserBatchDeleteRequest implements Serializable {

    private List<Long> userIdList;

    private static final long serialVersionUID = 1L;
}
