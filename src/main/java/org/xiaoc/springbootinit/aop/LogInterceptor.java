package org.xiaoc.springbootinit.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 对执行的接口进行日志打印 统计执行时间等信息
 */
@Slf4j
@Aspect
@Component
public class LogInterceptor {

    @Around("execution (* org.xiaoc.springbootinit.controller.*.*(..))")
    public Object doIntercept(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 执行方法
        Object result = joinPoint.proceed();
        //停止计时
        stopWatch.stop();
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取方法名
        String methodName = methodSignature.getMethod().getName();
        // 打印日志
        log.info("执行方法:{} 耗时:{}毫秒", methodName, stopWatch.getTime());
        return result;
    }

}
