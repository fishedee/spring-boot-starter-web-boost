package com.fishedee.web_boost.sample;

import com.fishedee.web_boost.LogTimeHandlerInterceptor;
import com.fishedee.web_boost.WebBoostMvcConfig;
import com.fishedee.web_boost.WebBoostRequestResolver;
import com.fishedee.web_boost.autoconfig.WebBoostProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebBoostMvcConfig{
}
