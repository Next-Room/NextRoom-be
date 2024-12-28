package com.nextroom.nextRoomServer.config;

import com.nextroom.nextRoomServer.filter.ContentCachingLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ContentCachingLoggingFilter> requestCachingFilter() {
        FilterRegistrationBean<ContentCachingLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentCachingLoggingFilter());
        registrationBean.setOrder(0); // 가장 먼저 실행
        return registrationBean;
    }
}
