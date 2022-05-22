package com.project.onlinechat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.onlinechat.dto.ApiResponse;
import com.project.onlinechat.dto.Update;
import com.project.onlinechat.entity.Chat;
import com.project.onlinechat.entity.Member;
import com.project.onlinechat.entity.Message;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.UpdateType;
import com.project.onlinechat.repository.ChatRepository;
import com.project.onlinechat.repository.MessageRepository;
import com.project.onlinechat.repository.UserRepository;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    final UserRepository userRepository;
    final ChatRepository chatRepository;
    final AuthService authService;
    final MessageRepository messageRepository;
    final SimpMessageSendingOperations sendingOperations;

    @GetMapping
    public String getAllChats(HttpServletRequest req, HttpServletResponse res, Model model){
        User user = authService.getUserByRequest(req);
        if(user==null){
            return "redirect:/auth/login";
        }
        List<Chat> chats = chatRepository.findAllById(chatRepository.findByMember(user.getId()));
        List<Message> lastMessages=new ArrayList<>();
        model.addAttribute("chats", chats);
        model.addAttribute("user", user);
        for (Chat chat : chats) {
            List<Message> lastMessage = messageRepository.findByChat_IdOrderByTimeAsc(chat.getId(), Pageable.ofSize(1));
            if(!lastMessage.isEmpty()){
                lastMessages.add(lastMessage.get(0));
            }
        }
        model.addAttribute("lastMessages",lastMessages);
        return "chat";
    }



    @MessageMapping("/chat.open")
    public void getChat(SimpMessageHeaderAccessor headerAccessor, @Payload Long id){
        if(headerAccessor.getSessionAttributes()!=null){
            User user = (User)headerAccessor.getSessionAttributes().get("user");
            Optional<Chat> optionalChat = chatRepository.findById(id);
            Map<String, Object> response=new LinkedHashMap<>();
            if(optionalChat.isPresent()){
                response.put("messages", messageRepository.findByChat_IdOrderByTimeAsc(id, Pageable.ofSize(20)));
                response.put("chat", optionalChat.get());
                sendingOperations.convertAndSend("/topic/chat/open/"+user.getId(),
                        ApiResponse.builder()
                        .success(true)
                        .message("Chat opened")
                        .data(response)
                        .build());
            }
        }
    }


    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message confirm) {
        messageRepository.save(confirm);
    }

}
