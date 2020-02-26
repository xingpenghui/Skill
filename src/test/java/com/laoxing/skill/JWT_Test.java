package com.laoxing.skill;

import com.laoxing.skill.util.JwtUtil;
import org.junit.jupiter.api.Test;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-26 10:02
 */
public class JWT_Test {
    @Test
    public void t1(){
        String token= JwtUtil.createToken("Hello Jwt!");
        System.out.println(token);
        System.out.println("解析："+JwtUtil.parseToken(token));
    }
}
