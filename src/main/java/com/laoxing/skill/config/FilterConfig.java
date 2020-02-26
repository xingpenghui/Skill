package com.laoxing.skill.config;

import com.laoxing.skill.filter.LimitBucketFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: Skill
 * @description: 注册过滤器
 * @author: Feri
 * @create: 2020-02-26 16:48
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean addFilter(LimitBucketFilter filter){
        FilterRegistrationBean bean=new FilterRegistrationBean();
        bean.addUrlPatterns("/*");
        bean.setFilter(filter);
        return bean;
    }
}