package com.example.servcie;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @program: spring-boot-practice
 * @description: key生成器, 具体实现由使用者自己去注入
 * @author: xrwang8
 * @create: 2020-12-18 10:59
 **/
public interface CacheKeyGenerator {

    /***
     * 获取AOP参数,生成指定缓存Key
     */
    String getLockKey(ProceedingJoinPoint proceedingJoinPoint);


}
