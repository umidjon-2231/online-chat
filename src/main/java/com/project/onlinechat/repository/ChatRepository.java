package com.project.onlinechat.repository;

import com.project.onlinechat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(nativeQuery = true, value = "select chat_id as id from chat_members" +
            "    join member m on m.id = chat_members.members_id" +
            " where m.id=:id")
    List<Long> findByMember(Long id);
}