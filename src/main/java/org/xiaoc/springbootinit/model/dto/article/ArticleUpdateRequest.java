package org.xiaoc.springbootinit.model.dto.article;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ArticleUpdateRequest implements Serializable {

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





    private static final long serialVersionUID = 1L;
}
