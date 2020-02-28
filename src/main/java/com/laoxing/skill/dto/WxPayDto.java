package com.laoxing.skill.dto;

import lombok.Data;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-28 11:42
 */
@Data
public class WxPayDto {
    private String body;
    private String out_trade_no;
    private int total_fee;//单位分
}
