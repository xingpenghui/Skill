package com.laoxing.skill.listener;

import com.laoxing.skill.config.RedisKeyConfig;
import com.laoxing.skill.dao.OrderDao;
import com.laoxing.skill.entity.Order;
import com.laoxing.skill.service.impl.OrderServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 15:17
 */
@Component
@RabbitListener(queues = "skill.queue.dlxorder")
public class DlxOrderListener {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderServiceImpl service;

    @RabbitHandler
    public void handler(Map<String,Object> mqmap){
        //获取订单
        Order order= (Order) mqmap.get("order");
        int count= (int) mqmap.get("count");
        if(order!=null){
            //查询数据库对应的订单的支付状态
            Order morder=orderDao.selectById(order.getOid());
            if(morder.getStatus()==order.getStatus()){
                //15分钟 之后 订单状态未变  未支付
                //15分未支付 订单超时 释放库存
                orderDao.update(order.getOid(),7);//更改订单状态为超时
                //将订单中的库存添加到 Redis  释放库存
                int c = (int) redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_GOODS,
                        order.getSgid());
                redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_GOODS,
                        order.getSgid(),c+count);
                //更改预减库存的内存标记
                if(service.getMap().containsKey(order.getSgid())){
                    //之前的内存标记 商品售罄 恢复
                    service.getMap().remove(order.getSgid());
                }
            }
        }
    }
}
