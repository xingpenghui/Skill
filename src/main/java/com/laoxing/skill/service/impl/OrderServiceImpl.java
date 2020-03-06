package com.laoxing.skill.service.impl;

import com.alibaba.fastjson.JSON;
import com.laoxing.skill.config.RedisKeyConfig;
import com.laoxing.skill.dao.OrderDao;
import com.laoxing.skill.dao.SkillGoodsDao;
import com.laoxing.skill.dto.SkillGoodsDto;
import com.laoxing.skill.entity.Order;
import com.laoxing.skill.entity.SkillGoods;
import com.laoxing.skill.exception.OrderException;
import com.laoxing.skill.service.OrderService;
import com.laoxing.skill.util.IdGenerator;
import com.laoxing.skill.util.PathUtil;
import com.laoxing.skill.vo.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
    private IdGenerator idGenerator=new IdGenerator();
    @Autowired
    private SkillGoodsDao goodsDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private StringRedisTemplate redisTemplate;
    //辅助 Redis 实现预减库存 记录已售罄的商品
    private ConcurrentHashMap<Integer,Boolean> map=new ConcurrentHashMap<>();
    public ConcurrentHashMap getMap(){
        return map;
    }
    @Autowired
    private RabbitTemplate rabbitTemplate;

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

    @Override
    public R saveV2(SkillGoodsDto goodsDto) throws OrderException {
        int uid=0;
        //每个用户 只能秒杀成功一次 同种商品  这种
        // 1.验证购买上限
        if (goodsDto.getCount() > maxCount) {
            //直接失败
            //失败信息的记录
            return R.fail("秒杀的数量超过上限");
        }
        else {
            //2.查询Redis中的秒杀商品信息 校验秒杀商品是否存在
            if(redisTemplate.opsForHash().hasKey(RedisKeyConfig.SKILL_GOODS,
                    goodsDto.getGid())){
                //3.校验库存是否足够  内存标记 是否已售罄
                if(map.containsKey(goodsDto.getGid())){
                    return R.fail("商品已售罄");
                }
                else {
                    int c = (int) redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_GOODS,
                            goodsDto.getGid());
                    //校验库存是否足够
                    if (c >= goodsDto.getCount()) {
                        // 4.校验用户是否购买过此秒杀商品
                        if (!redisTemplate.opsForHash().hasKey(RedisKeyConfig.SKILL_ORDER,
                                goodsDto.getGid() + ":" + uid)) {
                            //5.生成订单 ---Redis
                            Order order1 = new Order();
                            order1.setOid(idGenerator.nextId());
                            order1.setStatus(1);
                            order1.setSgid(goodsDto.getGid());
                            order1.setUid(uid);
                            redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_ORDER,
                                    goodsDto.getGid() + ":" + uid, JSON.toJSON(order1));
                            //RabbitMQ --消息机制 异步操作 Mysql中订单的生成
                            Map<String,Object> mqmap=new HashMap<>();
                            mqmap.put("count",goodsDto.getCount());
                            mqmap.put("order",order1);
                            rabbitTemplate.convertAndSend(
                                    "skill.exchange.order",
                                    mqmap);
                            //6.订单生成成功-Redis中的库存 进行预减
                            redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_GOODS, goodsDto.getGid(), c - goodsDto.getCount());
                            if(c-goodsDto.getCount()==0){
                                map.put(goodsDto.getGid(),true);
                            }
                            //秒杀的真正结束
                            return R.ok(order1);
                        } else {
                            return R.fail("亲，你已经购买过此秒杀商品");
                        }
                    } else {
                        return R.fail("亲，秒杀商品库存不足");
                    }
                }
            } else {
                return R.fail("秒杀商品不存在");
            }
        }
    }

    @Override
    public R checkSkill(int sgid) {
        //1.校验 sgid 秒杀商品是否存在
        if(redisTemplate.opsForHash().hasKey(RedisKeyConfig.SKILL_GOODS,
                sgid)){
            //2. 校验 秒杀商品对应的path 是否存在
            if(redisTemplate.hasKey(RedisKeyConfig.SKILL_API+sgid)){
                //如果存在 说明 之前的未失效 返回
                return R.ok(redisTemplate.opsForValue().get(RedisKeyConfig.SKILL_API+sgid));

            }else {
                //要么是第一次请求秒杀接口，要么是之前的失效了
                String p= PathUtil.createPathPass(sgid);
                redisTemplate.opsForValue().set(RedisKeyConfig.SKILL_API+sgid,
                       p,RedisKeyConfig.API_TIME, TimeUnit.SECONDS);
                return R.ok(p);
            }
        }else {
            return R.fail("亲，非法请求");
        }
    }

    @Override
    public R saveV3(String path, SkillGoodsDto goodsDto) throws OrderException {
        //校验 对应的 路径变量是否存在
        if(!redisTemplate.hasKey(RedisKeyConfig.SKILL_API+goodsDto.getGid())){
            return R.fail("非法请求");
        }else {
            //校验 秒杀接口的路径变量 是否和服务端一致
            String p=redisTemplate.opsForValue().get(RedisKeyConfig.SKILL_API+goodsDto.getGid());
            if(Objects.equals(p,path)){
                //说明路径 合法
                int uid=0;
                //每个用户 只能秒杀成功一次 同种商品  这种
                // 1.验证购买上限
                if (goodsDto.getCount() > maxCount) {
                    //直接失败
                    //失败信息的记录
                    return R.fail("秒杀的数量超过上限");
                }
                else {
                    //2.查询Redis中的秒杀商品信息 校验秒杀商品是否存在
                    if(redisTemplate.opsForHash().hasKey(RedisKeyConfig.SKILL_GOODS,
                            goodsDto.getGid())){
                        //3.校验库存是否足够  内存标记 是否已售罄
                        if(map.containsKey(goodsDto.getGid())){
                            return R.fail("商品已售罄");
                        }
                        else {
                            int c = (int) redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_GOODS,
                                    goodsDto.getGid());
                            //校验库存是否足够
                            if (c >= goodsDto.getCount()) {
                                // 4.校验用户是否购买过此秒杀商品
                                if (!redisTemplate.opsForHash().hasKey(RedisKeyConfig.SKILL_ORDER,
                                        goodsDto.getGid() + ":" + uid)) {
                                    //5.生成订单 ---Redis
                                    Order order1 = new Order();
                                    order1.setOid(idGenerator.nextId());
                                    order1.setStatus(1);
                                    order1.setSgid(goodsDto.getGid());
                                    order1.setUid(uid);
                                    redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_ORDER,
                                            goodsDto.getGid() + ":" + uid, JSON.toJSON(order1));
                                    //RabbitMQ --消息机制 异步操作 Mysql中订单的生成
                                    Map<String,Object> mqmap=new HashMap<>();
                                    mqmap.put("count",goodsDto.getCount());
                                    mqmap.put("order",order1);
                                    rabbitTemplate.convertAndSend(
                                            "skill.exchange.order",
                                            mqmap);
                                    //6.订单生成成功-Redis中的库存 进行预减
                                    redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_GOODS, goodsDto.getGid(), c - goodsDto.getCount());
                                    if(c-goodsDto.getCount()==0){
                                        map.put(goodsDto.getGid(),true);
                                    }
                                    //秒杀的真正结束
                                    return R.ok(order1);
                                } else {
                                    return R.fail("亲，你已经购买过此秒杀商品");
                                }
                            } else {
                                return R.fail("亲，秒杀商品库存不足");
                            }
                        }
                    } else {
                        return R.fail("秒杀商品不存在");
                    }
                }
            }else {
                return R.fail("非法请求");
            }
        }
    }
}
