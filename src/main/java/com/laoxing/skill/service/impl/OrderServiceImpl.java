package com.laoxing.skill.service.impl;

import com.laoxing.skill.dao.OrderDao;
import com.laoxing.skill.dao.SkillGoodsDao;
import com.laoxing.skill.dto.SkillGoodsDto;
import com.laoxing.skill.entity.Order;
import com.laoxing.skill.entity.SkillGoods;
import com.laoxing.skill.exception.OrderException;
import com.laoxing.skill.service.OrderService;
import com.laoxing.skill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 11:05
 */
@Service
public class OrderServiceImpl implements OrderService {
    //注入属性的值
    @Value("${skill.maxcount}")
    private int maxCount;//秒杀的商品数量的上限  每个用户

    @Autowired
    private SkillGoodsDao goodsDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public R save(SkillGoodsDto goodsDto) throws OrderException {
        //每个用户 只能秒杀成功一次 同种商品  这种
        // 1.验证购买上限
        if (goodsDto.getCount() > maxCount) {
            //直接失败
            //失败信息的记录
            return R.fail("秒杀的数量超过上限");
        }
        else {
            //2.查询数据库的秒杀商品信息 校验秒杀商品是否存在
            SkillGoods kgoods = goodsDao.selectById(goodsDto.getGid());
            if (kgoods != null) {
                //3.秒杀活动时间的比较
                //当前时间
                Date curr = new Date();
                if (curr.getTime() < kgoods.getStime().getTime()) {
                    return R.fail("秒杀活动还未开始");
                } else if (curr.getTime() > kgoods.getEtime().getTime()) {
                    return R.fail("秒杀活动已经结束");
                } else {
                    //活动进行中
                    //4.校验用户是否购买过此秒杀商品
                    Order order = orderDao.selectByUid(0, goodsDto.getGid());
                    if (order == null) {
                        //当前用户 对于当前的秒杀商品 没有购买过
                        // 5.校验库存是否足够
                        if (kgoods.getSstock() >= goodsDto.getCount()) {
                            //6.生成订单
                            Order order1 = new Order();
                            if (orderDao.insert(order1) > 0) {
                                //订单生成成功
                                //7.扣减库存：1.下单立减库存 2.付款立减库存 3.预减库存
                                //更改数据库
                                if (goodsDao.update(kgoods.getSkid(),
                                        kgoods.getSstock() - goodsDto.getCount()) > 0) {
                                    //秒杀的真正结束
                                    return R.ok(order1);
                                }else {
                                   throw new OrderException("库存变更失败");
                                }
                            } else {
                                return R.fail("亲，网络不行，请刷新试试");
                            }
                        }else {
                           return R.fail("亲，秒杀商品已售罄！");
                        }
                    } else {
                        return R.fail("亲，你已经购买过此秒杀商品");
                    }
                }
            } else {
                return R.fail("秒杀商品不存在");
            }
        }
    }
}
