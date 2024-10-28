package org.xiaoc.springbootinit.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiaoc.springbootinit.utils.NetUtils;

@RestController
@RequestMapping("/test")
public class Test {

    @GetMapping("/1")
    public String test1(HttpServletRequest request) {
        return NetUtils.getIpAddress(request);
    }
}
