package com.laoxing.skill.controller;

import com.laoxing.skill.dto.SkillGoodsDto;
import com.laoxing.skill.exception.OrderException;
import com.laoxing.skill.service.OrderService;
import com.laoxing.skill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 14:23
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService service;

    @PostMapping("/api/order/skillsave.do")
    public R save(@RequestBody SkillGoodsDto goodsDto) throws OrderException {
        return service.save(goodsDto);
    }

    @PostMapping("/api/order/v2/skillsave.do")
    public R savev2(@RequestBody SkillGoodsDto goodsDto) throws OrderException {
        return service.save(goodsDto);
    }
}
