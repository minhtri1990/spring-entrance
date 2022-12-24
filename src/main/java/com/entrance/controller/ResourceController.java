package com.entrance.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @RequestMapping("/user")
    public String getUser() {
        return "Hello User";
    }

    @RequestMapping("/admin")
    public String getAdmin() {
        return "Hello Admin";
    }

}
