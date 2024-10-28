package org.xiaoc.springbootinit.common;

import lombok.Data;
import org.xiaoc.springbootinit.constant.PageConstant;

/**
 * 分页请求
 */
@Data
public class PageRequest {

    /**
     *当前页
     */
    private int current;

    /**
     * 每页显示条数
     */
    private int pageSize;

    /**
     * 排序字段
     */
    private String sortFiled;

    /**
     * 排序方式 （默认升序排序）
     */
    private String sortOrder = PageConstant.SORT_ORDER_ASC;

}
