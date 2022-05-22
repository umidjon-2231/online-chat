package com.project.onlinechat.interceptor;

import com.project.onlinechat.entity.User;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.nio.file.AccessDeniedException;

public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getSessionAttributes()!=null && accessor.getCommand()!=null) {
            User user = (User) accessor.getSessionAttributes().get("user");
            System.out.println(accessor.getSessionAttributes());
            if(user!=null){
                switch (accessor.getCommand()){
                    case SUBSCRIBE -> {
                        String destination = (String)message.getHeaders().get("simpDestination");
                        if(destination!=null && destination.startsWith("/topic/update")){
                            if(destination.endsWith("/"+user.getId())){
                                return message;
                            }else {
                                throw new Exception("Access denied!");
                            }
                        }
                    }
                }
            }else {
//                throw new Exception("Access denied!");
            }
            return message;
        }
        return message;
    }
}
