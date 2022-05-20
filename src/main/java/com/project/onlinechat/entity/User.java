package com.project.onlinechat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.onlinechat.entity.enums.Status;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(unique = true, nullable = false)
    private String username, email;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
