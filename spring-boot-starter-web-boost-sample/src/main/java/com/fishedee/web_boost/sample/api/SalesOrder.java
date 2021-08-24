package com.fishedee.web_boost.sample.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrder {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item{
        @NotBlank
        private String name;

        @Min(1)
        private int count;
    }

    @NotBlank
    private String name;

    @NotNull
    private int age;

    @NotEmpty
    //这个@Valid注解不能缺少，否则，Item里面的Validator校验不生效
    @Valid
    private List<Item> itemList;
}

