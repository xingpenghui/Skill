package com.laoxing.skill.dao;

import com.laoxing.skill.entity.User;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:30
 */
public interface UserDao {
    int  insert(User user);
    User selectByPhone(String phone);
}
