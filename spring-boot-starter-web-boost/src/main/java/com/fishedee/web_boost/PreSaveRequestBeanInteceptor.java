package com.fishedee.web_boost;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class PreSaveRequestBeanInteceptor implements HandlerInterceptor {
    public static String PRESAVE_BEAN_CLASS = "webboost.pre_save.bean_class";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if( handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class beanType = handlerMethod.getBeanType();
            request.setAttribute(PRESAVE_BEAN_CLASS,beanType);
        }
        return true;
    }
}
