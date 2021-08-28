package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.List;

public class CurdController<T1,T2> {
    @GetMapping("/get")
    public String get(T1 id){
        return id.toString();
    }

    @PostMapping("/add")
    public String add(T2 data){
        return data.toString();
    }
}
