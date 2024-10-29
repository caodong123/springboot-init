package org.xiaoc.springbootinit.model.dto.article;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleDeleteRequest implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

}
