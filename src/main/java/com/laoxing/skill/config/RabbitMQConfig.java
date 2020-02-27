package com.laoxing.skill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 14:40
 */
@Configuration
public class RabbitMQConfig {
    //1.定义变量
    //定义 队列 名称
    public String qname1="skill.queue.order";//正常队列 同步Mysql订单数据
    public String qname2="skill.queue.ttlorder"; //延迟队列 设置消息有效期 默认秒杀 15分钟
    public String qname3="skill.queue.dlxorder";//死信队列 进行校验订单是否完成支付
    //定义 交换器名称
    public String exchange1="skill.exchange.order";// fanout 直接转发 用户订单数据发送
    public String exchange2="skill.exchange.dlxorder";// direct 路由匹配 用户订单数据发送
    //定义路由规则名称
    public String routingkey1="skillorderdlx";//死信队列的 路由匹配
    // 订单的超时时间
    public int msgttl=15*60;//默认的秒杀订单的超时时间 15分钟

    //2.创建 该创建  2个交换器 3个队列
    //创建普通队列
    @Bean
    public Queue createQ1(){
        return new Queue(qname1);
    }
    //创建延迟队列
    @Bean
    public Queue createQ2(){
        //设置队列的参数
        Map<String,Object> args=new HashMap<>();
        //设置对应的参数：1.时间
        args.put("x-message-ttl",msgttl*1000);
        //设置 死信队列的交换器
        args.put("x-dead-letter-exchange",exchange2);
        //设置 死信队列的路由关键字
        args.put("x-dead-routing-key",routingkey1);
        return QueueBuilder.durable(qname2).withArguments(args).build();
    }
    //创建死信队列
    @Bean
    public Queue createQ3(){
        return new Queue(qname3);
    }
    //创建交换器 fanout 转发订单消息
    @Bean
    public FanoutExchange createEx1(){
        return new FanoutExchange(exchange1);
    }
    //创建交换器 direct 延迟队列的超时消息 转发到死信队列
    @Bean
    public DirectExchange createEx2(){
        return new DirectExchange(exchange2);
    }

    //3.绑定 将队列和交换器 绑定起一起
    @Bean
    public Binding createB1(){
        return BindingBuilder.bind(createQ1()).to(createEx1());
    }
    @Bean
    public Binding createB2(){
        return BindingBuilder.bind(createQ2()).to(createEx1());
    }
    @Bean
    public Binding createB3(){
        return BindingBuilder.bind(createQ3()).to(createEx2()).with(routingkey1);
    }

}