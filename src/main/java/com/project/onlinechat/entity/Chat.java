package com.project.onlinechat.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String title;
    @ManyToOne()
    @JoinColumn(nullable = false)
    private User owner;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime created=LocalDateTime.now();
    @Column(nullable = false)
    @Builder.Default
    private boolean active=true;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Member> members;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Attachment photo;
}
