package org.xiaoc.springbootinit.aop;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xiaoc.springbootinit.annotation.AuthCheck;
import org.xiaoc.springbootinit.common.ErrorCode;
import org.xiaoc.springbootinit.exception.BusinessException;
import org.xiaoc.springbootinit.exception.ThrowUtils;
import org.xiaoc.springbootinit.model.vo.LoginUserVO;
import org.xiaoc.springbootinit.service.UserService;

import java.util.Objects;

@Slf4j
@Component
@Aspect
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 拦截有AuthCheck注解的方法
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取需要的权限
        String needAuth = authCheck.Role();
        // 获取当前用户
        LoginUserVO loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 判断是否拥有权限
        if(!needAuth.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 执行方法
        return joinPoint.proceed();
    }

}
