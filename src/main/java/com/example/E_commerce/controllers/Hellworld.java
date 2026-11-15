package com.example.E_commerce.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api")
public class Hellworld {

    @GetMapping("/hello")
    public String hi(){
        return "Hello world";
    }
}
