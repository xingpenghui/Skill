package com.laoxing.skill.dto;

import lombok.Data;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 11:01
 */
@Data
public class SkillGoodsDto {
    private String token;//令牌 前后端分离
    private int gid;//描述商品的id
    private int count;//数量 内部做上限控制
    private String type;//渠道类型 pc app small
}