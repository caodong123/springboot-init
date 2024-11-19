package org.xiaoc.springbootinit.model.dto.article;

import lombok.Data;
import org.xiaoc.springbootinit.common.PageRequest;

import java.io.Serializable;

@Data
public class ArticleGetMineRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
}
