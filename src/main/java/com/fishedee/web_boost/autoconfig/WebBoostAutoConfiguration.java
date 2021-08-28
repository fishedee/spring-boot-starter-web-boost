package com.fishedee.web_boost.autoconfig;


import com.fishedee.web_boost.LogTimeHandlerInterceptor;
import com.fishedee.web_boost.PreSaveRequestBeanInteceptor;
import com.fishedee.web_boost.WebBoostRequestResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Slf4j
@Configuration
@EnableConfigurationProperties(WebBoostProperties.class)
public class WebBoostAutoConfiguration {
    private final AbstractApplicationContext applicationContext;

    private final WebBoostProperties properties;

    public WebBoostAutoConfiguration(AbstractApplicationContext applicationContext, WebBoostProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(LogTimeHandlerInterceptor.class)
    @ConditionalOnProperty(value = "spring.web-boost.enable", havingValue = "true")
    public LogTimeHandlerInterceptor logTimeHandler() {
        return new LogTimeHandlerInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(PreSaveRequestBeanInteceptor.class)
    @ConditionalOnProperty(value = "spring.web-boost.enable", havingValue = "true")
    public PreSaveRequestBeanInteceptor preSaveRequestBeanInteceptor() {
        return new PreSaveRequestBeanInteceptor();
    }

    @Bean
    @ConditionalOnMissingBean(WebBoostRequestResolver.class)
    @ConditionalOnProperty(value = "spring.web-boost.enable", havingValue = "true")
    public WebBoostRequestResolver requestResolver() {
        return new WebBoostRequestResolver();
    }
}