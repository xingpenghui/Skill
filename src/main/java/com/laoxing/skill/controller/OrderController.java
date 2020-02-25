package com.laoxing.skill.controller;

import com.laoxing.skill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
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


}
