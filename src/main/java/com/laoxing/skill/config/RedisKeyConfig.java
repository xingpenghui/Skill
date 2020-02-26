package com.laoxing.skill.config;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-26 11:06
 */
public class RedisKeyConfig {

    //记录登录的令牌  --校验令牌
    public static final String TOKEN_USER="skill:token:";//有效期1小时,后面跟令牌
    //记录登录的账号  --实现唯一登录
    public static final String USER_TOKEN="skill:user:";//有效期1小时,后面跟手机号

    public static final int TOKEN_HOURS=1;//有效期 令牌

    //记录被挤掉的令牌
    public static final String TOKEN_SWAP="skill:token:swap";//后面记录令牌 ，SET

    //记录错误的次数 5分钟内的错误次数
    public static final String USER_PASSFAIL="skill:userfail:";//String 手机号:时间戳 值 有效期5分钟

    public static final int TOKEN_FAIL=5;//有效期 5分钟

    //记录冻结的账号
    public static final String USER_FREEZE="skill:userfreeze:";//后面跟手机号 String 有效期 30分钟

    public static final int USER_FREEZE_TIME=30;//有效期 30分钟
}
