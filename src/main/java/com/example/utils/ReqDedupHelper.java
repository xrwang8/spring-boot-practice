package com.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * @program: spring-boot-practice
 * @description:
 * @author: xrwang8
 * @create: 2020-12-18 09:17
 **/
@Slf4j
public class ReqDedupHelper {


    /***
     * @Description:
     * @Param: reqJSON 请求的参数，这里通常是JSON
     * @Param: excludeKeys 请求参数里面要去除哪些字段再求摘要
     * @return: 去除参数的MD5摘要
     * @Author: xrwang8
     * @Date: 2020/12/18
     */

    ObjectMapper objectMapper = new ObjectMapper();

    public String dedupParamMD5(final String reqJSON, String... excludeKeys) throws IOException {
        String decreptParam = reqJSON;
        TreeMap paramTreeMap = objectMapper.readValue(decreptParam, TreeMap.class);
        if (excludeKeys != null) {
            List<String> dedupExcludeKeys = Arrays.asList(excludeKeys);
            if (!CollectionUtils.isEmpty(dedupExcludeKeys)) {
                for (String dedupExcludeKey : dedupExcludeKeys) {
                    paramTreeMap.remove(dedupExcludeKey);
                }
            }
        }

        String paramTreeMapJSON = objectMapper.writeValueAsString(paramTreeMap);
        String md5deDupParam = jdkMD5(paramTreeMapJSON);
        log.debug("md5deDupParam = {}, excludeKeys = {} {}", md5deDupParam, Arrays.deepToString(excludeKeys), paramTreeMapJSON);
        return md5deDupParam;

    }

    private static String jdkMD5(String src) {
        String res = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] mdBytes = messageDigest.digest(src.getBytes());
            res = DatatypeConverter.printHexBinary(mdBytes);
        } catch (Exception e) {
            log.error("", e);
        }
        return res;
    }

}
