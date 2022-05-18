package com.project.onlinechat.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Value("${spring.sql.init.mode:always}")
    private String initMode;

    @Override
    public void run(String... args) throws Exception {
        if(initMode.equalsIgnoreCase("always")){

        }
    }
}
