package com.project.onlinechat.controller;

import com.project.onlinechat.dto.ApiResponse;
import com.project.onlinechat.dto.LoginDto;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

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

    @PostMapping("/register")
    public String addUser(@ModelAttribute LoginDto dto, Model model) {
        ApiResponse<User> apiResponse = authService.register(dto);
        if(apiResponse.isSuccess()){
            return "redirect:/auth/login";
        }
        model.addAttribute("error", apiResponse.getMessage());
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

    @GetMapping("/main")
    @ResponseBody
    public HttpEntity<?> getUser(HttpServletRequest req){
        User user = authService.getUserByRequest(req);
        ApiResponse<User> apiResponse=ApiResponse.<User>builder()
                .success(user!=null)
                .data(user)
                .message(user!=null?"Success!":"Token failed!")
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


}
