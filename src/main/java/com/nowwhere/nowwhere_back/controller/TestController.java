package com.nowwhere.nowwhere_back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        System.out.println("called!");
        return "Hello i'm nowWhere_backend!";
    }
}
