package com.laoxing.skill.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: Skill
 * @description: 秒杀商品类
 * @author: Feri
 * @create: 2020-02-25 10:29
 */
@Data
public class SkillGoods {
    private int skid; //秒杀商品的id

    private Date stime; //开始时间
    private Date etime;//结束时间
    private int sstock;//秒杀商品的库存量
}