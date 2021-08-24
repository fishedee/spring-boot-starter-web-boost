![Release](https://jitpack.io/v/fishedee/spring-boot-starter-web-boost.svg)
(https://jitpack.io/#fishedee/spring-boot-starter-web-boost)

# web_boost

SpringBoost的Web工具库，功能包括有：

* 全局返回格式统一，转换为{code:0,msg:'',data:{xxx}}的格式
* 全局异常捕捉，抛出异常后，自动转换为以上格式
* 更轻松的输入值处理，GET与POST请求默认使用JSON格式传递，支持多个参数绑定到同一个JSON输入上
* 更轻松的输入校验，在数据或者接口上直接写@NotNull，@Min，这些校验注解就可以轻松做输入校验了

## 安装

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.fishedee</groupId>
    <artifactId>spring-boot-starter-web-boost</artifactId>
    <version>1.0</version>
</dependency>
```

在项目的pom.xml加入以上配置即可

## 使用

代码在[这里](https://github.com/fishedee/spring-boot-starter-web-boost/tree/master/spring-boot-starter-web-boost-sample)

```ini
# 开启web-boost
spring.web-boost.enable=true
```

初始化配置

```java
package com.fishedee.web_boost.sample;

import com.fishedee.web_boost.WebBoostMvcConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig extends WebBoostMvcConfig {
}
```

配置WebConfig，做输入格式的默认处理

```java
package com.fishedee.web_boost.sample;

import com.fishedee.web_boost.WebBoostResponseAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.fishedee.web_boost.sample")
public class WebResponseAdvice extends WebBoostResponseAdvice {
}
```

配置WebResponseAdvice，做返回格式的默认处理

```java
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
}
```

配置WebExceptionAdvice，全局异常处理。这里是支持自定义异常格式处理的，具体可以看Sample

```java
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    //http://localhost:9090/user/go1
    @GetMapping("/go1")
    public String go1(){
        return "123";
    }
}
```

对/user/go1的返回值是{code:0,msg:'',data:"123"}

```java
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
        return "result_"+salesOrderId;
    }

    //正常，http://localhost:9090/salesOrder/get2，值为0
    //正常，http://localhost:9090/salesOrder/get2?data=%7B%22salesOrderId%22%3A789%7D，值为789
    @GetMapping("/get2")
    public String get2(int salesOrderId){
        return "result3_"+salesOrderId;
    }

    //错误，http://localhost:9090/salesOrder/get3，非空
    //正常，http://localhost:9090/salesOrder/get3?data=%7B%22name%22%3A%22fish%22%2C%22age%22%3A123%2C%22itemList%22%3A%5B%7B%22name%22%3A%22m1%22%2C%22count%22%3A2%7D%5D%7D，值为
    @GetMapping("/get3")
    public SalesOrder get6(@NotNull  SalesOrder salesOrder){
        return salesOrder;
    }
}
```

GET请求，可以指定用@RequestParam来获取参数，或者省略，默认在data参数中传入JSON格式化数据

```java
package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/salesOrder2")
@Validated
public class SalesOrderController2 {
    @PostMapping("/post")
    public String post(int salesOrderId){
        return "result_"+salesOrderId;
    }

    @PostMapping("/post2")
    public SalesOrder post2(@NotNull  SalesOrder salesOrder){
        return salesOrder;
    }
}
```

POST请求，默认就在请求体里面放入JSON格式化数据就可以了

```java
package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mix")
@Validated
public class MixController {

    //salesOrderId来自于param，salesOrder来自于data参数JSON格式化数据
    @GetMapping("/get")
    public Map get(@RequestParam int salesOrderId, SalesOrder salesOrder){
        Map result = new HashMap();
        result.put("salesOrderId",salesOrderId);
        result.put("salesOrder",salesOrder);
        return result;
    }

    //salesOrderId与salesOrder来自于data参数的JSON格式化数据
    @GetMapping("/get2")
    public Map get2(int salesOrderId, SalesOrder salesOrder){
        Map result = new HashMap();
        result.put("salesOrderId",salesOrderId);
        result.put("salesOrder",salesOrder);
        return result;
    }

    //salesOrderId与salesOrder来自于POST表单的JSON格式化数据
    @PostMapping("/post")
    public Map post(int salesOrderId, SalesOrder salesOrder){
        Map result = new HashMap();
        result.put("salesOrderId",salesOrderId);
        result.put("salesOrder",salesOrder);
        return result;
    }
}
```

混合请求，直接从Query参数，或者请求体中读取输入参数



