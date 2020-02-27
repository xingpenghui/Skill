package com.laoxing.skill.listener;

import com.laoxing.skill.dao.OrderDao;
import com.laoxing.skill.dao.SkillGoodsDao;
import com.laoxing.skill.entity.Order;
import com.laoxing.skill.entity.SkillGoods;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 15:09
 */
@Component
@RabbitListener(queues = "skill.queue.order")
public class OrderListener {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SkillGoodsDao goodsDao;

    @RabbitHandler
    public void handler(Map<String,Object> mqmap){
        Order order= (Order) mqmap.get("order");
        int c= (int) mqmap.get("count");
        if(order!=null){
            //查询数据库中的秒杀商品的价格
            SkillGoods goods=goodsDao.selectById(order.getSgid());
            order.setTprice(goods.getSprice()*c);
            orderDao.insert(order);
        }
    }
}