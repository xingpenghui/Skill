package com.laoxing.skill.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:30
 */
@Data
public class Order {
    private Integer oid;
    private int sgid; //秒杀商品id
    private Double tprice;
    private Date ctime;
    private Integer status;//订单状态
    private int uid;
    private int type;//订单类型 1：普通 2：秒杀
}