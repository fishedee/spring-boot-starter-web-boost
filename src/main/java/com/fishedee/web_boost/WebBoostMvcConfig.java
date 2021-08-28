package com.fishedee.web_boost;

import com.fishedee.web_boost.autoconfig.WebBoostProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fish on 2021/5/31.
 */
public class WebBoostMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebBoostProperties webBoostProperties;

    @Autowired
    private LogTimeHandlerInterceptor logTimeHandlerInterceptor;

    @Autowired
    private PreSaveRequestBeanInteceptor preSaveRequestBeanInteceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if( webBoostProperties.isEnable() == false ) {
            return;
        }
        registry.addInterceptor(logTimeHandlerInterceptor);
        registry.addInterceptor(preSaveRequestBeanInteceptor);
    }

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Autowired
    private WebBoostRequestResolver requestModelArgumentResolver;

    @PostConstruct
    public void injectSelfMethodArgumentResolver() {
        if( webBoostProperties.isEnable() == false ) {
            return;
        }
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
        argumentResolvers.add(requestModelArgumentResolver);
        argumentResolvers.addAll(adapter.getArgumentResolvers());
        adapter.setArgumentResolvers(argumentResolvers);
    }
}