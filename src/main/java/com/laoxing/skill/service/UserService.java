package com.laoxing.skill.service;

import com.laoxing.skill.dto.LoginDto;
import com.laoxing.skill.vo.R;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-26 10:08
 */
public interface UserService {
    //唯一登录
    R login(LoginDto dto);
    //校验令牌 的有效性
    R checkToken(String token);

}
