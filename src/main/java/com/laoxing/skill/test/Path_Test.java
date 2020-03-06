package com.laoxing.skill.test;

import com.laoxing.skill.util.EncryptUtil;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-03-06 16:19
 */
public class Path_Test {
    public static void main(String[] args) {
        String s="1001";
        String m= EncryptUtil.sha(EncryptUtil.SHA1,s+System.currentTimeMillis());
        System.out.println(m);


    }
}
