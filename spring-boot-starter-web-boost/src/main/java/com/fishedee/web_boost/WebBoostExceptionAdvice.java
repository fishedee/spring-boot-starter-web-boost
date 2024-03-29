package com.fishedee.web_boost;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * Created by fish on 2021/4/25.
 */
@Slf4j
public class WebBoostExceptionAdvice {

    /*
    404找不到异常,一般不需要重写,因为statusCode为404
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public MyResponseBodyAdvice.ResponseResult exceptionHandler(Exception e){
        return new MyResponseBodyAdvice.ResponseResult(10001,e.getMessage(),null);
    }
    */

    //拦截其他错误
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult exceptionHandler(Exception e){
        log.error("server exception {}",e);
        LogTimeHandlerInterceptor.setException(e);
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.INTERNAL_SERVER_ERROR,500,"Internal Server Error",null);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    @ResponseBody
    public Object exceptionMyHandler(HttpMessageNotWritableException e){
        log.error("server exception {}",e);
        return null;
    }

    //拦截我们自定义的错误
    @ExceptionHandler(WebBoostException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult exceptionMyHandler(WebBoostException e){
        log.error("business exception {}",e);
        LogTimeHandlerInterceptor.setException(e);
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,e.getCode(),e.getMessage(),e.getData());
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult exceptionMyHandler(HttpMessageConversionException e){
        log.info("argument exception {}",e);
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1,"请求格式转换错误:"+e.getCause().getMessage(),null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult exceptionMyHandler(HttpMessageNotReadableException e){
        log.info("argument exception {}",e);
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1,"请求格式错误:"+e.getCause().getMessage(),null);
    }

    //请求方法不存在
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult exceptionMyHandler(HttpRequestMethodNotSupportedException e){
        log.info("argument exception {}",e);
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.NOT_FOUND,1,"不受支持的请求方法",null);
    }

    //https://xbuba.com/questions/51828879,直接返回String会报错
    //localhost:8080/dog/go1?grade=0,缺少一个classroom请求参数
    @ResponseBody
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public WebBoostResponseAdvice.ResponseResult doMissingServletRequestParameterHandler(MissingServletRequestParameterException e) {
        log.info("argument exception {}",e);
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1,"缺少请求参数:"+e.getMessage(),null);
    }

    //将参数绑定到基础类型时报错
    //localhost:8080/dog/go1?grade=0&classroom=3,参数校验不合法,[grade年级只能从1-9]
    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public WebBoostResponseAdvice.ResponseResult  ConstraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.info("argument exception {}",ex);
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        List<String> msgList = new ArrayList<>();
        while (iterator.hasNext()) {
            ConstraintViolation<?> cvl = iterator.next();
            msgList.add(cvl.getPropertyPath()+":"+cvl.getMessage());
        }
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1,msgList.toString(),null);
    }

    //post localhost:8080/sheep/go3
    //{
    //    "items":[
    //    ]
    //}
    //post请求时将参数绑定到对象时报错
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult  doMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.info("argument exception {}",ex);
        BindingResult result = ex.getBindingResult();
        List<String> msgList = new ArrayList<String>();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            ObjectError error=errors.get(0);
            msgList.add(ex.getParameter()+":"+error.getDefaultMessage());
        }
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1,msgList.toString(),null);
    }

    //localhost:8080/sheep/go1?count=-1
    //get请求时将参数绑定到对象时报错
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public WebBoostResponseAdvice.ResponseResult handleBindException(BindException ex) {
        log.info("argument exception {}",ex);
        List<FieldError> bindingResult = ex.getBindingResult().getFieldErrors();
        List<String> msgList = new ArrayList<String>();
        for (FieldError fieldError : bindingResult) {
            msgList.add(fieldError.getField()+":"+fieldError.getDefaultMessage());
        }
        return new WebBoostResponseAdvice.ResponseResult(HttpStatus.OK,1,msgList.toString(),null);
    }

}