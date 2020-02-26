package com.laoxing.skill.filter;

import com.alibaba.fastjson.JSON;
import com.laoxing.skill.config.RedisKeyConfig;
import com.laoxing.skill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: Skill
 * @description: 实现限流算法 ：令牌桶算法
 * @author: Feri
 * @create: 2020-02-26 16:21
 */
@Component
public class LimitBucketFilter implements Filter {
    //令牌桶：按照一定的速度生成令牌，请求去令牌桶(容量池) 获取令牌。令牌桶有上限（QPS）
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //只对秒杀接口进行 限流
        if(request.getQueryString().endsWith("/api/order/skillsave.do")){
            //进行限流
            //取到令牌放行  令牌一旦取出---就要删除
            Long i= Long.parseLong(redisTemplate.opsForList().leftPop(RedisKeyConfig.LIMIT_BUCKET));
            if(i!=null){
                //取到  放行
                filterChain.doFilter(request,servletResponse);
            }else {
                //取不到  就拦截
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println(JSON.toJSONString(R.fail("亲，网络拥堵，暂且等待")));
            }
        }
    }
}
