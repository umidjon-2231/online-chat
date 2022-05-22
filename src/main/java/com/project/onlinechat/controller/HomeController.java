package com.project.onlinechat.controller;

import com.project.onlinechat.entity.User;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class HomeController {
    final AuthService authService;
    @GetMapping
    public String homaPage(HttpServletRequest req){
        User user = authService.getUserByRequest(req);
        if(user==null){
            return "redirect:/auth/login";
        }
        return "redirect:/chat";
    }
}
