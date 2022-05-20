package com.project.onlinechat.listener;

import com.project.onlinechat.entity.Message;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info(event.toString());
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");

        if(username != null) {
            logger.info("User Disconnected : " + username);
            Message message= Message.builder()
                    .type(MessageType.LEAVED)
                    .from(User.builder()
                            .username(username)
                            .build())
                    .build();

            messagingTemplate.convertAndSend("/topic/publicChatRoom", message);
        }
    }
}
