package com.fishedee.web_boost;

import com.fishedee.web_boost.autoconfig.WebBoostProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fish on 2021/5/31.
 */
@Slf4j
public class WebBoostMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebBoostProperties webBoostProperties;

    @Autowired
    private LogTimeHandlerInterceptor logTimeHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if( webBoostProperties.isEnable() == false ) {
            return;
        }
        registry.addInterceptor(logTimeHandlerInterceptor);
    }
}