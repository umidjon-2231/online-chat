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
    @ManyToOne
    private User owner;
    private LocalDateTime created;
    private boolean active;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Member> members;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Attachment photo;
}