package com.entrance.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @Operation(description = "user domain")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getUser() {
        return "Hello User";
    }

    @Operation(description = "admin domain")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdmin() {
        return "Hello Admin";
    }

}
