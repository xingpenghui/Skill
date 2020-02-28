package com.laoxing.skill.controller;

import io.goeasy.GoEasy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-28 16:10
 */
@RestController
public class GoEasyController {
    GoEasy goEasy = new GoEasy( "https://rest-hangzhou.goeasy.io", "BC-36808744fcda4503a8e8367e44297e9f");

    @GetMapping("/api/chat/msg")
    public String sendMsg(){
        goEasy.publish("my_channel", "Hello, GoEasy!");
        return "OK";
    }
}
