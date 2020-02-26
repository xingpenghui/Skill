package com.laoxing.skill.service.impl;

import com.alibaba.fastjson.JSON;
import com.laoxing.skill.config.RedisKeyConfig;
import com.laoxing.skill.dao.UserDao;
import com.laoxing.skill.dto.LoginDto;
import com.laoxing.skill.dto.TokenDto;
import com.laoxing.skill.entity.User;
import com.laoxing.skill.service.UserService;
import com.laoxing.skill.util.EncryptUtil;
import com.laoxing.skill.util.JwtUtil;
import com.laoxing.skill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-26 10:09
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Value("${skill.passkey}")
    private String passkey;

    //模板对象
    @Autowired
    private StringRedisTemplate redisTemplate;

    //唯一登录
    @Override
    public R login(LoginDto dto) {
        //1.校验该账号是否被冻结
        if(redisTemplate.hasKey(RedisKeyConfig.USER_FREEZE+dto.getPhone())){
            //该账号被冻结
            return R.fail("亲，账号已冻结，剩余解封时间："+
                    redisTemplate.getExpire(RedisKeyConfig.USER_FREEZE+dto.getPhone())+"秒");
        }else {
            //2、检查账号密码
            User user = userDao.selectByPhone(dto.getPhone());
            if (user != null) {
                //3、校验密码  注意密文
                if (user.getPassword().equals(EncryptUtil.aesenc(passkey, dto.getPwd()))) {
                    //账号和密码正确
                    //4.校验账户上是否登录过 唯一登录 如果登录过，那么本次就需要将原来的令牌干掉
                    if (redisTemplate.hasKey(RedisKeyConfig.USER_TOKEN + dto.getPhone())) {
                        //该账号之前登陆过
                        String t = redisTemplate.opsForValue().get(RedisKeyConfig.USER_TOKEN + dto.getPhone());
                        //记录到挤掉 信息中
                        redisTemplate.opsForSet().add(RedisKeyConfig.TOKEN_SWAP, t);
                        //同时删除原来的令牌
                        redisTemplate.delete(RedisKeyConfig.TOKEN_USER + t);
                    }
                    //5、生成令牌
                    TokenDto tokenDto = new TokenDto();
                    tokenDto.setPhone(dto.getPhone());
                    tokenDto.setSdate(new Date());
                    String token = JwtUtil.createToken(JSON.toJSONString(tokenDto));
                    //6、记录令牌--到Redis  五种数据类型
                    //记录令牌 对应的用户
                    redisTemplate.opsForValue().set(RedisKeyConfig.TOKEN_USER + token,
                            JSON.toJSONString(user), RedisKeyConfig.TOKEN_HOURS, TimeUnit.HOURS);
                    //记录账号对应的令牌
                    redisTemplate.opsForValue().set(RedisKeyConfig.USER_TOKEN + dto.getPhone(),
                            token, RedisKeyConfig.TOKEN_HOURS, TimeUnit.HOURS);
                    //7、令牌返回
                    return R.ok(token);
                } else {
                    //密码错误 5分钟内 密码错误3次以上 冻结30分钟
                    int r = 0;
                    //8.校验失败次数 是否冻结账号
                    Set set = redisTemplate.keys(
                            RedisKeyConfig.USER_PASSFAIL + dto.getPhone());
                    if (set != null && set.size() > 1) {
                        r = set.size();
                        //需要冻结 之前错误至少2次+这一次  三次
                        redisTemplate.opsForValue().set(RedisKeyConfig.USER_FREEZE + dto.getPhone(),
                                System.currentTimeMillis() / 1000 + "",
                                RedisKeyConfig.USER_FREEZE_TIME, TimeUnit.MINUTES);
                    }
                    //9.记录本次的失败
                    redisTemplate.opsForValue().set(RedisKeyConfig.USER_FREEZE + dto.getPhone()
                                    + ":" + System.currentTimeMillis(), "", RedisKeyConfig.TOKEN_FAIL,
                            TimeUnit.MINUTES);
                    r += 1;
                    return R.fail("亲，你已经失败" + r + "次数，小心账号被冻结哟！");
                }
            } else {
                return R.fail("账号或密码不正确！");
            }
        }
    }

    @Override
    public R checkToken(String token) {
        if(redisTemplate.hasKey(RedisKeyConfig.TOKEN_SWAP+token)){
            //说明账号被挤掉了
            return R.fail("亲，你的账号已经在其他设备上进行登录，您可以重新登录");
        }else {
            if(redisTemplate.hasKey(RedisKeyConfig.TOKEN_USER+token)){
                //令牌有效
                return R.ok();
            }else {
                //无效 1.有效期结束 2.修改密码
                return R.fail("亲，登录已失效，请重新登录");
            }
        }
    }

    @Override
    public R register(LoginDto dto) {
        return null;
    }

    @Override
    public R checkPhone(String phone) {
        return null;
    }
}
