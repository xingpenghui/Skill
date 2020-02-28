package com.laoxing.skill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-28 15:17
 */
@Configuration
public class WebSocketConfig {
    //支持WebSocket
    @Bean
    public ServerEndpointExporter createSEE(){
        return new ServerEndpointExporter();
    }

}
