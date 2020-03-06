package com.laoxing.skill.service;

import com.laoxing.skill.dto.SkillGoodsDto;
import com.laoxing.skill.entity.SkillGoods;
import com.laoxing.skill.exception.OrderException;
import com.laoxing.skill.vo.R;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:54
 */
public interface OrderService {
    //需要什么参数 ：令牌、商品id、数量
    R save(SkillGoodsDto goodsDto) throws OrderException;

    //V2.0接口
    R saveV2(SkillGoodsDto goodsDto) throws OrderException;

    //秒杀接口的动态化 URL 解决秒杀接口的隐藏
    R checkSkill(int sgid);
    //V3.0接口
    R saveV3(String path,SkillGoodsDto goodsDto) throws OrderException;
}