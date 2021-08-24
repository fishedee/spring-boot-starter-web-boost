package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class CurdController<T1,T2> {
    @GetMapping("/get")
    public String get(T1 groupId){
        return groupId.toString();
    }

    @PostMapping("/add")
    public String add(T2 data){
        return data.toString();
    }
}
