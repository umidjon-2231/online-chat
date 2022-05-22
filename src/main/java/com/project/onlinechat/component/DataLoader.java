package com.project.onlinechat.component;

import com.project.onlinechat.entity.Chat;
import com.project.onlinechat.entity.Member;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.ChatType;
import com.project.onlinechat.entity.enums.Permission;
import com.project.onlinechat.entity.enums.Role;
import com.project.onlinechat.entity.enums.Status;
import com.project.onlinechat.repository.ChatRepository;
import com.project.onlinechat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Value("${spring.sql.init.mode:always}")
    private String initMode;
    final ChatRepository chatRepository;
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if(initMode.equalsIgnoreCase("always")){
            User owner = userRepository.save(User.builder()
                    .status(Status.OFFLINE)
                    .email("tumidjon808@gmail.com")
                    .username("umidjon-2231")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
            chatRepository.save(Chat.builder()
                            .title("PDP chat")
                            .type(ChatType.PUBLIC)
                            .members(List.of(Member.builder()
                                            .user(owner)
                                            .permissions(List.of(Permission.SEND_MESSAGE))
                                            .role(Role.OWNER)
                                    .build()))
                            .owner(owner)
                    .build());
        }
    }
}
