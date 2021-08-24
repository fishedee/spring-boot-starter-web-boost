package com.fishedee.web_boost.sample;

import com.fishedee.web_boost.LogTimeHandlerInterceptor;
import com.fishedee.web_boost.WebBoostExceptionAdvice;
import com.fishedee.web_boost.WebBoostResponseAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class WebExceptionAdvice extends WebBoostExceptionAdvice {
    //自定义异常的拦截返回
    @ExceptionHandler(MyException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult exceptionHandler(MyException e){
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1001,e.getMessage(),null);
    }
}
