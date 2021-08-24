package com.fishedee.web_boost.sample.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/template")
@Validated
public class TemplateController extends CurdController<Long,User>{
}
