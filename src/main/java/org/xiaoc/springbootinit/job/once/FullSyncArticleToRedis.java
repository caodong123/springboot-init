package org.xiaoc.springbootinit.job.once;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.xiaoc.springbootinit.constant.ArticleConstant;
import org.xiaoc.springbootinit.model.entity.Article;
import org.xiaoc.springbootinit.service.ArticleService;

import java.util.List;

/**
 * 全量同步文章到redis
 */

@Component
@Slf4j
public class FullSyncArticleToRedis implements CommandLineRunner {

    @Resource
    private ArticleService articleService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(String... args) throws Exception {
        List<Article> articleList = articleService.list();
        if(CollUtil.isEmpty(articleList)){
            log.info("文章为空");
            return;
        }
        final int batchSize = 500;
        int total = articleList.size();
        for(int i = 0; i < total; i += batchSize){
            int end = Math.min(i + batchSize, total);
            log.info("从数据库中批量读取第{}行到第{}行数据", i, end);
            // 批量获取数据
            List<Article> batchList = articleList.subList(i, end);
            //将数据转换为json字符串
            List<String> jsonList = batchList.stream().map(JSONUtil::toJsonStr).toList();
            //获取id列表
            List<Long> idList = batchList.stream().map(Article::getId).toList();
            //使用pipelined批量写入redis
            List<Object> results = stringRedisTemplate.executePipelined(
                    (RedisCallback<Object>) redisConnection -> {
                        for (int j = 0; j < jsonList.size(); j++) {
                            Long id = idList.get(j);
                            String json = jsonList.get(j);
                            redisConnection.set((ArticleConstant.ARTICLE_CACHE + id).getBytes(), json.getBytes());
                        }
                        return null;
                    }
            );
        }
    }
}
