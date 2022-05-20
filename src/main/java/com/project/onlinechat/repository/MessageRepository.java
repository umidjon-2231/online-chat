package com.project.onlinechat.repository;

import com.project.onlinechat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChat_IdOrderByTimeAsc(Long id, Pageable pageable);
}