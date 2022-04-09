package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/salesOrder2")
@Validated
public class SalesOrderController2 {
    @PostMapping("/post3")
    public String post3(int salesOrderId){
        return "result3_"+salesOrderId;
    }

    @PostMapping("/post4")
    public String post4(Integer salesOrderId){
        return "result4_"+salesOrderId;
    }

    @PostMapping("/post5")
    public String post5(Integer salesOrderId){
        return "result5_"+salesOrderId;
    }

    @PostMapping("/post6")
    public SalesOrder post6(SalesOrder salesOrder){
        return salesOrder;
    }


    @GetMapping("getBatch")
    public String getBatch(List<String> ids){
        return "DD"+ids.toString();
    }

}
