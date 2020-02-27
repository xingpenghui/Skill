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
    private Long oid;
    private int sgid; //秒杀商品id
    private Double tprice;
    private Date ctime;
    /**
     * 订单状态:1 未支付 2支付成功,未发货 3.已发货，待收货
     * 4.收货完成，待确认 5 确认收货，未评价 6 订单完成,已评价
     * 7.订单超时 8.取消订单(未付款) 9.退款 10.退货 */
    private int status;//
    private int uid;
    private int type;//订单类型 1：普通 2：秒杀
}