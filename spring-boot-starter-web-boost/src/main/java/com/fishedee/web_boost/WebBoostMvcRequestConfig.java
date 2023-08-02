package com.fishedee.web_boost;

import com.fishedee.web_boost.autoconfig.WebBoostProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebBoostMvcRequestConfig {

    @Autowired
    private WebBoostProperties webBoostProperties;

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
