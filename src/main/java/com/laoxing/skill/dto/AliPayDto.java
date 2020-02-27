package com.laoxing.skill.dto;

import lombok.Data;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 16:30
 */
@Data
public class AliPayDto {
    private String out_trade_no; //订单号
//    private String product_code="FAST_INSTANT_TRADE_PAY"; //商品销售码
    private double total_amount;//订单总金额，单位为元，精确到小数点后两位
    private String subject;//订单标题
}