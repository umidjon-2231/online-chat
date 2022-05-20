package com.project.onlinechat.controller;

import com.project.onlinechat.dto.ApiResponse;
import com.project.onlinechat.dto.LoginDto;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Value("${auth.token.cookie}")
    String cookieName;
    @Value("${auth.token.expire}")
    Long expire;

    private final AuthService authService;

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto dto, Model model, HttpServletResponse res){
        ApiResponse<String> apiResponse = authService.login(dto);
        if(apiResponse.isSuccess()){
            Cookie token=new Cookie(cookieName, apiResponse.getData());
            token.setSecure(true);
            token.setPath("/");
            token.setMaxAge(expire.intValue()/1000);
            res.addCookie(token);
            return "redirect:/chat";
        }
        model.addAttribute("error", apiResponse.getMessage());
        return "login";
    }
}
