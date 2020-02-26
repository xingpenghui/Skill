package com.laoxing.skill;

import com.laoxing.skill.util.EncryptUtil;
import org.junit.jupiter.api.Test;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-26 10:26
 */
public class Pwd_Test {
    @Test
    public void t1(){
        String key= EncryptUtil.createAESKey();
        System.out.println(key);
        String pass="123456";
        String mw=EncryptUtil.aesenc(key,pass);
        System.out.println("密文："+mw);
        System.out.println("解密："+EncryptUtil.aesdec(key,mw));
    }
}
