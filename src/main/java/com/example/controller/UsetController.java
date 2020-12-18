package com.example.controller;

import com.example.annotation.CacheLock;
import com.example.annotation.CacheParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-boot-practice
 * @description:
 * @author: xrwang8
 * @create: 2020-12-18 11:13
 **/


@RestController
@RequestMapping("/test")
public class UsetController {

    @CacheLock(prefix = "user")
    @GetMapping("/user")
    public String query(@CacheParam(name = "token") @RequestParam String token) {
        return "success - " + token;
    }


}
