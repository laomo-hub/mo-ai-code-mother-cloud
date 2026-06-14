package com.mo.moaicodemother.aop;

import com.mo.moaicodemother.annotation.AuthCheck;
import com.mo.moaicodemother.entity.User;
import com.mo.moaicodemother.enums.UserRoleEnum;
import com.mo.moaicodemother.exception.BusinessException;
import com.mo.moaicodemother.exception.ErrorCode;
import com.mo.moaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {


    @Resource
    private UserService userService;

    /**
     * 拦截器
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck)throws Throwable{
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttribute = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttribute).getRequest();
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //不需要权限，直接放行
        if(mustUserRoleEnum == null){
            return joinPoint.proceed();
        }
        //以下的代码，必须有这个权限才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        //没有权限，直接拒绝
        if (userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //要求必须有管理员权限，但当前登录用户没有
        if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)&& !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        //通过普通用户的权限校验，放行
        return joinPoint.proceed();

    }
}
