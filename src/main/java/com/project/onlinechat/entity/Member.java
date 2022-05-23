package com.project.onlinechat.entity;

import com.project.onlinechat.entity.enums.Permission;
import com.project.onlinechat.entity.enums.Role;
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
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Permission> permissions;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastView=LocalDateTime.now();
}
