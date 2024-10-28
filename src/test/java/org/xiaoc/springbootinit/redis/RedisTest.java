package org.xiaoc.springbootinit.redis;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {

    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testRedis(){
        redisTemplate.opsForValue().set("name","xiaoc");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }

}
