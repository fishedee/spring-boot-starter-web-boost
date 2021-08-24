package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mix")
@Validated
public class MixController {

    //salesOrderId来自于param，salesOrder来自于data参数JSON序列化
    @GetMapping("/get")
    public Map get(@RequestParam int salesOrderId, SalesOrder salesOrder){
        Map result = new HashMap();
        result.put("salesOrderId",salesOrderId);
        result.put("salesOrder",salesOrder);
        return result;
    }

    //salesOrderId与salesOrder来自于data参数的JSON序列化
    @GetMapping("/get2")
    public Map get2(int salesOrderId, SalesOrder salesOrder){
        Map result = new HashMap();
        result.put("salesOrderId",salesOrderId);
        result.put("salesOrder",salesOrder);
        return result;
    }

    //salesOrderId与salesOrder来自于POST表单的JSON序列化
    @PostMapping("/post")
    public Map post(int salesOrderId, SalesOrder salesOrder){
        Map result = new HashMap();
        result.put("salesOrderId",salesOrderId);
        result.put("salesOrder",salesOrder);
        return result;
    }
}
