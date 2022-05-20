package com.project.onlinechat.controller;

import com.project.onlinechat.entity.Chat;
import com.project.onlinechat.entity.Message;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.repository.ChatRepository;
import com.project.onlinechat.repository.UserRepository;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    final UserRepository userRepository;
    final ChatRepository chatRepository;
    final AuthService authService;

    @GetMapping
    public String getAllChats(HttpServletRequest req, HttpServletResponse res, Model model){
        User user = authService.getByToken(req);
        if(user==null){
            return "redirect:/auth/login";
        }
        List<Chat> chats = chatRepository.findAllById(chatRepository.findByMember(user.getId()));
        List<Message> lastMessages;
        model.addAttribute("chats", chats);
        model.addAttribute("user", user);
        return "chat";
    }
}
