package cn.itcast.cas.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.security.util.SecurityConstants;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/user")
@RestController
public class UserController {

    @GetMapping("/getUsername")
    public String getUsername(HttpServletRequest request){
        System.out.println("userName = " + request.getRemoteUser());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username;
    }
}
