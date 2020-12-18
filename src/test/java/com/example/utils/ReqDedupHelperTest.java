package com.example.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @program: spring-boot-practice
 * @description:
 * @author: xrwang8
 * @create: 2020-12-18 09:45
 **/
class ReqDedupHelperTest {

    //两个请求一样，但是请求时间差一秒
    String req = "{\n" +
            "\"requestTime\" :\"20190101120001\",\n" +
            "\"requestValue\" :\"1000\",\n" +
            "\"requestKey\" :\"key\"\n" +
            "}";

    String req2 = "{\n" +
            "\"requestTime\" :\"20190101120002\",\n" +
            "\"requestValue\" :\"1000\",\n" +
            "\"requestKey\" :\"key\"\n" +
            "}";

    /***
     * @Description: 全参数比对，所以两个参数MD5不同
     * @Param: []
     * @return: void
     * @Author: xrwang8
     * @Date: 2020/12/18
     */


    @Test
    void dedupParamMD5() throws IOException {

        String dedupMD5 = new ReqDedupHelper().dedupParamMD5(req);
        String dedupMD52 = new ReqDedupHelper().dedupParamMD5(req2);
        assertFalse(dedupMD5.equals(dedupMD52));


    }

    /***
     * @Description: 去除请求参数MD5相同
     * @Param: []
     * @return: void
     * @Author: xrwang8
     * @Date: 2020/12/18
     */
    @Test
    void dedupParamWith() throws IOException {
        String dedupMD53 = new ReqDedupHelper().dedupParamMD5(req, "requestTime");
        String dedupMD54 = new ReqDedupHelper().dedupParamMD5(req2, "requestTime");
        assertTrue(dedupMD53.equals(dedupMD54));


    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /***
     * @Description: 测试重复请求
     * @Param: []
     * @return: void
     * @Author: xrwang8
     * @Date: 2020/12/18
     */
    @Test
    void repeatRequest() throws IOException {
        //模拟用户id
        String userId = "12345678";
        //模拟测试的请求接口
        String method = "pay";
        String dedupMD5 = "C2A36FED15128E9E878583CAAAFEFDE9";
        String KEY = "dedup:U=" + userId + "M=" + method + "P=" + dedupMD5;
        // 1000毫秒过期，1000ms内的重复请求会认为重复
        long expireTime = 1000;
        long expireAt = System.currentTimeMillis() + expireTime;
        String val = "expireAt@" + expireAt;
        // NOTE:直接SETNX不支持带过期时间，所以设置+过期不是原子操作，极端情况下可能设置了就不过期了，后面相同请求可能会误以为需要去重，所以这里使用底层API，保证SETNX+过期时间是原子操作
        Boolean firstSet = StringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection
                .set(KEY.getBytes(), val.getBytes(), Expiration.milliseconds(expireTime),
                        RedisStringCommands.SetOption.SET_IF_ABSENT));


        final boolean isConsiderDup;
        if (firstSet != null && firstSet) {
            isConsiderDup = false;
        } else {
            isConsiderDup = true;
        }


    }


}