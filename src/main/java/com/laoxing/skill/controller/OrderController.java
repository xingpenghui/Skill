package com.laoxing.skill.controller;

import com.laoxing.skill.dto.SkillGoodsDto;
import com.laoxing.skill.exception.OrderException;
import com.laoxing.skill.service.OrderService;
import com.laoxing.skill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    //
    @PostMapping("/api/order/v2/skillsave.do")
    public R savev2(@RequestBody SkillGoodsDto goodsDto) throws OrderException {
        return service.saveV2(goodsDto);
    }
    //秒杀接口的隐藏实现
    @PostMapping("/api/order/{path}/skillsave.do")
    public R savev3(@PathVariable("path") String path,SkillGoodsDto goodsDto) throws OrderException {
        return service.saveV3(path,goodsDto);
    }
    //采用外层接口限制 --- 返回真正的秒杀接口
    @GetMapping("/api/order/skill.do")
    public R query(int sgid) throws OrderException {
        return service.checkSkill(sgid);
    }
}