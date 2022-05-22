package com.project.onlinechat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.onlinechat.entity.enums.ChatType;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Where(clause = "active=true")
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
    @JsonIgnore
    private boolean active=true;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<Member> members;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Attachment photo;
    @Column(nullable = false)
    private ChatType type;
}
