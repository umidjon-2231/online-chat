package com.project.onlinechat.listener;

import com.project.onlinechat.entity.Message;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.MessageType;
import com.project.onlinechat.entity.enums.Status;
import com.project.onlinechat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event){
//        System.err.println(event);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if(headerAccessor.getSessionAttributes()!=null){
            System.err.println("Disconnected: "+headerAccessor.getSessionAttributes().get("username"));
            User user = (User) headerAccessor.getSessionAttributes().get("user");
            if(user != null) {
                user.setStatus(Status.OFFLINE);
                userRepository.save(user);
            }
        }

    }
}
