package com.project.onlinechat.entity;

import com.project.onlinechat.entity.enums.MessageType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime time=LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User from;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Chat chat;
}
