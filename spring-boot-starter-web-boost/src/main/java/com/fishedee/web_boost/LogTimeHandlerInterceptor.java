package com.fishedee.web_boost;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Slf4j
public class LogTimeHandlerInterceptor implements HandlerInterceptor {

    //全局注入
    @Autowired
    private MeterRegistry meterRegistry;

    private ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<Exception> expectionLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long start = System.currentTimeMillis();
        startTimeThreadLocal.set(start);
        return true;
    }

    public static void setException(Exception exception){
        expectionLocal.set(exception);
    }

    private static void clearException(){
        expectionLocal.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //加全局请求数
        meterRegistry.counter("http.requests").increment();

        if (handler instanceof HandlerMethod == false) {
            return;
        }
        try {
            Long startTime = startTimeThreadLocal.get();
            Long endTime = System.currentTimeMillis();

            StringBuilder logs = new StringBuilder();              //可在此处获取当前用户放日志信息里
            logs.append(" IP:").append(request.getRemoteAddr());//获取请求地址IP 自己实现
            if( ex == null ){
                //一般输出,对于Controller出现的异常,应该配合GlobalExceptionhandler来处理
                Exception businessException = expectionLocal.get();
                //使用完后要清空，否则下次还会读到这个错误，Tomcat的线程是复用的
                clearException();
                HandlerMethod method = (HandlerMethod) handler;
                String className = method.getBeanType().getName();
                String methodName = method.getMethod().getName();
                if( businessException == null){
                    meterRegistry.counter("http.requests.success").increment();
                    logs.append(" ").append(className).append("::").append(methodName);
                }else if( businessException instanceof WebBoostException){
                    meterRegistry.counter("http.requests.error").increment();
                    logs.append(" ").append(className).append("::").append(methodName).append("[ERROR]").append(businessException.getMessage());
                }else{
                    meterRegistry.counter("http.requests.crash").increment();
                    logs.append(" ").append(className).append("::").append(methodName).append("[CRASH]").append(businessException.getMessage());
                }
            }else{
                //只能捕捉意味的异常,不能捕捉普通controller的异常
                logs.append(" ").append(ex.getClass()).append("::").append(ex.getMessage());
            }
            long time = endTime - startTime;
            logs.append(" 耗时：").append(time).append("(ms)");
            log.info(logs.toString());

            String uri = request.getRequestURI();
            meterRegistry.timer("http.requests.uri","uri",uri).record(Duration.ofMillis(time));
        } finally {
            startTimeThreadLocal.remove();
        }
    }
}