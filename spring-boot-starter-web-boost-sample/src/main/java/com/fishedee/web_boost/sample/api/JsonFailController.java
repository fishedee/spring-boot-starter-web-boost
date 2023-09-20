package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/jsonFail")
@Validated
public class JsonFailController {

    public static class Data{
        public String getPrice(){
            return new BigDecimal("100").divide(new BigDecimal("0")).toPlainString();
        }
    }

    @GetMapping("/get")
    public Data get(){
        return new Data();
    }
}
