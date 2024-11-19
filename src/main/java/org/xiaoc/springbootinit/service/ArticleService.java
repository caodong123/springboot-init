package org.xiaoc.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.xiaoc.springbootinit.model.dto.article.ArticleQueryRequest;
import org.xiaoc.springbootinit.model.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xiaoc.springbootinit.model.entity.User;

import java.util.List;

/**
* @author CAODONG
* @description 针对表【article(文章)】的数据库操作Service
* @createDate 2024-10-29 11:33:51
*/
public interface ArticleService extends IService<Article> {

    void checkArticle(Article article);

    Wrapper<Article> getQueryWrapper(ArticleQueryRequest articleQueryRequest);

    boolean batchDeleteArticle(List<Long> articleIdList);
}
