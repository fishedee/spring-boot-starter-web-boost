package com.fishedee.web_boost;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
public class WebBoostResponseAdvice implements ResponseBodyAdvice {
    @Data
    @AllArgsConstructor
    public static class ResponseResult{
        @JsonIgnore
        private HttpStatus statusCode;

        private int code;

        private String msg;

        private Object data;


        public ResponseResult(int code,String message,Object data){
            this.code = code;
            this.msg = message;
            this.data = data;
            this.statusCode = HttpStatus.OK;
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ResponseResult result = null;

        //处理body的不同类型
        if (body == null) {
            result = new ResponseResult(0,"",null);
        }else if( body instanceof  String){
            //对于字符串类型，要特殊处理，及早返回
            result = new ResponseResult(0,"",body);
            try {
                return objectMapper.writeValueAsString(result);
            }catch (JsonProcessingException e ){
                throw new RuntimeException(e);
            }
        }else if ( body instanceof ResponseResult ) {
            result = (ResponseResult)body;
        }else{
            result = new ResponseResult(0,"",body);
        }

        //输出
        if( result.getStatusCode().isError() ){
            response.setStatusCode(result.getStatusCode());
        }
        return result;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class converterType) {
        return methodParameter.hasMethodAnnotation(NoWebBoostResponse.class) == false;
    }
}