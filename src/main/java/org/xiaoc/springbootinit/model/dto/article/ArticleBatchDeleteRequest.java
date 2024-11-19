package org.xiaoc.springbootinit.model.dto.article;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除文章请求
 */
@Data
public class ArticleBatchDeleteRequest implements Serializable {

    private List<Long> articleIdList;

    private static final long serialVersionUID = 1L;
}
