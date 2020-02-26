package com.laoxing.skill.controller;

import com.laoxing.skill.dto.LoginDto;
import com.laoxing.skill.service.UserService;
import com.laoxing.skill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-26 12:06
 */
@RestController
public class UserController {
    @Autowired
    private UserService service;

    //登录
    @PostMapping("/api/user/login.do")
    public R login(@RequestBody LoginDto dto){
        return service.login(dto);
    }
    //校验
    @GetMapping("/api/user/checktoken.do")
    public R check(String token){
        return service.checkToken(token);
    }
    //注册
    @PostMapping("/api/user/register.do")
    public R register(@RequestBody LoginDto dto){
        return service.register(dto);
    }
    //校验手机号是否注册
    @GetMapping("/api/user/checkphone.do")
    public R checkPhone(String phone){
        return service.checkPhone(phone);
    }
}
