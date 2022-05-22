package com.project.onlinechat.interceptor;

import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.Status;
import com.project.onlinechat.repository.UserRepository;
import com.project.onlinechat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeInterceptor.class);
    final AuthService authService;
    final UserRepository userRepository;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        logger.info("Before handshake");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("sessionId", session.getId());
            User userByRequest = authService.getUserByRequest(servletRequest.getServletRequest());
            if(userByRequest==null){
                return true;
            }
            userByRequest.setStatus(Status.ONLINE);
            attributes.put("user", userByRequest);
            attributes.put("username", userByRequest.getUsername());
            userRepository.save(userByRequest);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        logger.info("After handshake");

    }
}
