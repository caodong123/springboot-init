package org.xiaoc.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.xiaoc.springbootinit.model.entity.Article;
import org.xiaoc.springbootinit.service.ArticleService;
import org.xiaoc.springbootinit.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

/**
* @author CAODONG
* @description 针对表【article(文章)】的数据库操作Service实现
* @createDate 2024-10-29 11:33:51
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

}




