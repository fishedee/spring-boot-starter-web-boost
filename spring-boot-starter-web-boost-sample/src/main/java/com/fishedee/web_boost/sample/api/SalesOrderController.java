package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/salesOrder")
@Validated
public class SalesOrderController {

    //报错，http://localhost:9090/salesOrder/get，缺少参数
    //正常，http://localhost:9090/salesOrder/get?salesOrderId=123
    @GetMapping("/get")
    public String get(@RequestParam int salesOrderId){
        return "123_"+salesOrderId;
    }

    //报错，http://localhost:9090/salesOrder/get2?salesOrderId=9，范围不在10以上
    //正常，http://localhost:9090/salesOrder/get2?salesOrderId=456
    @GetMapping("/get2")
    public String get2(@RequestParam @Min(10) Integer salesOrderId){
        return "123_"+salesOrderId;
    }

    //正常，http://localhost:9090/salesOrder/get3，值为0
    //正常，http://localhost:9090/salesOrder/get3?data=%7B%22salesOrderId%22%3A789%7D，值为789
    @GetMapping("/get3")
    public String get3(int salesOrderId){
        return "123_"+salesOrderId;
    }

    //正常，http://localhost:9090/salesOrder/get4，值为null
    //正常，http://localhost:9090/salesOrder/get4?data=%7B%22salesOrderId%22%3A789%7D，值为789
    @GetMapping("/get4")
    public String get4(Integer salesOrderId){
        return "123_"+salesOrderId;
    }

    //错误，http://localhost:9090/salesOrder/get5，非空
    //正常，http://localhost:9090/salesOrder/get5?data=%7B%22salesOrderId%22%3A789%7D，值为789
    @GetMapping("/get5")
    public String get5(@NotNull Integer salesOrderId){
        return "123_"+salesOrderId;
    }

    //错误，http://localhost:9090/salesOrder/get6，非空
    //正常，http://localhost:9090/salesOrder/get6?data=%7B%22name%22%3A%22fish%22%2C%22age%22%3A123%2C%22itemList%22%3A%5B%7B%22name%22%3A%22m1%22%2C%22count%22%3A2%7D%5D%7D，值为
    @GetMapping("/get6")
    public SalesOrder get6(@NotNull  SalesOrder salesOrder){
        return salesOrder;
    }

    @PostMapping("/post1")
    public SalesOrder post1(@NotNull  SalesOrder salesOrder){
        return salesOrder;
    }

    @PostMapping("/post2")
    public SalesOrder post2(@NotNull  SalesOrder salesOrder,@NotNull Integer salesOrderId){
        salesOrder.setAge(salesOrderId+salesOrder.getAge());
        return salesOrder;
    }
}
