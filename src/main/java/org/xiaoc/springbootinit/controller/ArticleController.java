package org.xiaoc.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.xiaoc.springbootinit.common.BaseResponse;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.common.ResultUtils;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.model.dto.article.ArticleAddRequest;
import org.xiaoc.springbootinit.model.dto.article.ArticleDeleteRequest;
import org.xiaoc.springbootinit.model.dto.article.ArticleQueryRequest;
import org.xiaoc.springbootinit.model.entity.Article;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.service.ArticleService;
import org.xiaoc.springbootinit.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private UserService userService;

    @Resource
    private ArticleService articleService;

    /**
     * 添加文章
     * @param articleAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addArticle(@RequestBody ArticleAddRequest articleAddRequest, HttpServletRequest request){
        ThrowUtils.throwIf(articleAddRequest == null, ErrorCode.PARAMS_ERROR);
        Article article = new Article();
        // copy属性
        BeanUtils.copyProperties(articleAddRequest, article);
        List<String> tags = articleAddRequest.getTags();
        // list转json字符串
        if(tags != null && !tags.isEmpty()){
            article.setTags(JSONUtil.toJsonStr(tags));
        }
        // 设置作者
        LoginUserVO user = userService.getLoginUser(request);
        article.setUserId(user.getId());
        //校验文章
        articleService.checkArticle(article);
        // 保存文章
        boolean result = articleService.save(article);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加失败");
        return ResultUtils.success(article.getId());
    }

    /**
     * 删除文章
     * @param articleDeleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteArticle(@RequestBody ArticleDeleteRequest articleDeleteRequest){
        Long id = articleDeleteRequest.getId();
        boolean result = articleService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除失败");
        return ResultUtils.success(result);
    }

    /**
     * 更新文章
     * @param articleAddRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateArticle(@RequestBody ArticleAddRequest articleAddRequest){
        ThrowUtils.throwIf(articleAddRequest == null, ErrorCode.PARAMS_ERROR);
        Article article = new Article();
        // copy属性
        BeanUtils.copyProperties(articleAddRequest, article);
        // list转json字符串
        List<String> tags = articleAddRequest.getTags();
        if(tags != null && !tags.isEmpty()){
            article.setTags(JSONUtil.toJsonStr(tags));
        }
        //校验文章
        articleService.checkArticle(article);
        // 判断是否存在
        Article older = articleService.getById(article.getId());
        ThrowUtils.throwIf(older == null, ErrorCode.PARAMS_ERROR, "文章不存在");
        // 更新文章
        boolean result = articleService.updateById(article);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新失败");
        return ResultUtils.success(true);
    }

    /**
     * 根据id查询文章
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Article> getArticleById(Long id){
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        Article article = articleService.getById(id);
        ThrowUtils.throwIf(article == null, ErrorCode.PARAMS_ERROR, "文章不存在");
        return ResultUtils.success(article);
    }

    /**
     * 查询我的文章
     * @param request
     * @return
     */
    @GetMapping("/get/my")
    public BaseResponse<List<Article>> getMyArticles(HttpServletRequest request){
        // 获取登录用户
        LoginUserVO user = userService.getLoginUser(request);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR);
        Long userId = user.getId();
        // 根据id查询文章
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery(Article.class)
                .eq(Article::getUserId, userId);
        List<Article> articleList = articleService.list(wrapper);
        return ResultUtils.success(articleList);
    }

    /**
     * 分页查询文章
     * @param articleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Article>> listArticleByPage(@RequestBody ArticleQueryRequest articleQueryRequest, HttpServletRequest request){
        ThrowUtils.throwIf(articleQueryRequest==null,ErrorCode.PARAMS_ERROR);
        int current = articleQueryRequest.getCurrent();
        int pageSize = articleQueryRequest.getPageSize();
        //限制一次获取的数量
        ThrowUtils.throwIf( pageSize > 200,ErrorCode.PARAMS_ERROR);
        //获取查询wrapper
        Wrapper<Article> wrapper = articleService.getQueryWrapper(articleQueryRequest);
        IPage<Article> articleIPage = articleService.page(new Page<Article>(current, pageSize), wrapper);
        //获取数据
        Page<Article> articlePage = new Page<>(current,pageSize,articleIPage.getTotal());
        articlePage.setRecords(articleIPage.getRecords());
        return ResultUtils.success(articlePage);
    }



}
