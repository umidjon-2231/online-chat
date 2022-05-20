package com.project.onlinechat.controller;

import com.project.onlinechat.dto.ApiResponse;
import com.project.onlinechat.dto.LoginDto;
import com.project.onlinechat.entity.Message;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    final AuthService authService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicChatRoom")
    public Message sendMessage(@Payload Message chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/auth")
    public ApiResponse<User> addUser(@Payload LoginDto dto, SimpMessageHeaderAccessor headerAccessor) {
        ApiResponse<User> apiResponse = authService.register(dto);
        if(apiResponse.isSuccess()){
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", dto.getUsername());
        }
        return apiResponse;
    }
}
