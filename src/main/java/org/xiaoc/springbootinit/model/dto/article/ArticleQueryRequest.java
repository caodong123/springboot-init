package org.xiaoc.springbootinit.model.dto.article;

import lombok.Data;
import org.xiaoc.springbootinit.common.PageRequest;

import java.io.Serializable;
import java.util.List;

@Data
public class ArticleQueryRequest extends PageRequest implements Serializable {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 创建用户 id
     */
    private Long userId;



    private static final long serialVersionUID = 1L;
}
