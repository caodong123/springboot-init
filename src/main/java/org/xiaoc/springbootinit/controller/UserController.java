package org.xiaoc.springbootinit.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public String test(HttpServletRequest request){
        String method = request.getMethod();
        return method;
    }
}
