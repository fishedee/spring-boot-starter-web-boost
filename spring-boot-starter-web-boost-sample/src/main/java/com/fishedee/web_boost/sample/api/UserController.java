package com.fishedee.web_boost.sample.api;

import com.fishedee.web_boost.NoWebBoostResponse;
import com.fishedee.web_boost.WebBoostException;
import com.fishedee.web_boost.sample.MyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    //http://localhost:9090/user/go1
    @GetMapping("/go1")
    public String go1(){
        return "123";
    }

    //http://localhost:9090/user/go2
    @GetMapping("/go2")
    public void go2(){
        throw new WebBoostException(100,"go2 error",null);
    }

    //http://localhost:9090/user/go3
    @GetMapping("/go3")
    public void go3(){
        throw new RuntimeException("go3 error");
    }

    //http://localhost:9090/user/go4
    @GetMapping("/go4")
    public User go4(){
        return new User(1,"fish",(byte)12);
    }

    //http://localhost:9090/user/go5
    @GetMapping("/go5")
    public List<User> go5(){
        return Arrays.asList(new User(1,"fish",(byte)12),new User(2,"cat",(byte)78));
    }

    @GetMapping("/go6")
    public void go6(){
        throw  new MyException("go6_e");
    }

    @GetMapping("/go7")
    @NoWebBoostResponse
    public String go7(){
        return "cck";
    }
}
