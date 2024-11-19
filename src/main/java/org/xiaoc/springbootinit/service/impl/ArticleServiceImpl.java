package org.xiaoc.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.constant.PageConstant;
import org.xiaoc.springbootinit.exception.BusinessException;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.model.dto.article.ArticleQueryRequest;
import org.xiaoc.springbootinit.model.entity.Article;
import org.xiaoc.springbootinit.model.entity.User;
import org.xiaoc.springbootinit.service.ArticleService;
import org.xiaoc.springbootinit.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author CAODONG
* @description 针对表【article(文章)】的数据库操作Service实现
* @createDate 2024-10-29 11:33:51
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    /**
     * 校验文章信息
     * @param article
     */
    @Override
    public void checkArticle(Article article) {
        ThrowUtils.throwIf(article == null, ErrorCode.PARAMS_ERROR,"文章信息为空");
        String title = article.getTitle();
        String content = article.getContent();
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }

    }

    @Override
    public Wrapper<Article> getQueryWrapper(ArticleQueryRequest articleQueryRequest) {
        Long id = articleQueryRequest.getId();
        String title = articleQueryRequest.getTitle();
        String content = articleQueryRequest.getContent();
        List<String> tags = articleQueryRequest.getTags();
        Long userId = articleQueryRequest.getUserId();
        String sortFiled = articleQueryRequest.getSortFiled();
        String sortOrder = articleQueryRequest.getSortOrder();
        // 构建条件
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        // 精确查询  id
        wrapper.eq(id != null, "id", id);
        wrapper.eq(userId != null, "userId", userId);
        // 模糊查询  title/content
        wrapper.like(StringUtils.isNotBlank(title),"title",title);
        wrapper.like(StringUtils.isNotBlank(content),"content",content);
        // 集合查询  tags
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                wrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        wrapper.orderBy(StringUtils.isNotBlank(sortFiled),StringUtils.equals(sortOrder, PageConstant.SORT_ORDER_ASC),sortFiled);
        return wrapper;
    }

    /**
     * 批量删除文章 （事务管理）
     * @param articleIdList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteArticle(List<Long> articleIdList) {
        //校验数据
        ThrowUtils.throwIf(CollUtil.isEmpty(articleIdList), ErrorCode.PARAMS_ERROR);
        //批量删除
        for (Long id : articleIdList) {
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return true;
    }
}




