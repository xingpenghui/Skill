package com.laoxing.skill.dao;

import com.laoxing.skill.entity.SkillGoods;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:31
 */
public interface SkillGoodsDao {
    SkillGoods selectById(int gid);
    int update(int gid,int count);
}
