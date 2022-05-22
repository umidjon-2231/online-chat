package com.project.onlinechat.dto;

import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.UpdateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Update<T> {
    @Builder.Default
    private LocalDateTime time=LocalDateTime.now();
    @Builder.Default
    private UpdateType type=UpdateType.NEW_MESSAGE;
    private T data;
    private User user;

}
