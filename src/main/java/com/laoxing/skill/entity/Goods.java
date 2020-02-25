package com.laoxing.skill.entity;

import lombok.Data;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:29
 */
@Data
public class Goods {
    private Integer gid;
    private String gname;
    private Double gprice;
    private Double gstock;
    private String gimg;
}
