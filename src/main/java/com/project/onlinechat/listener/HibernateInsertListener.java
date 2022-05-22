package com.project.onlinechat.listener;

import com.project.onlinechat.dto.Update;
import com.project.onlinechat.entity.Member;
import com.project.onlinechat.entity.Message;
import com.project.onlinechat.entity.enums.UpdateType;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HibernateInsertListener implements PostInsertEventListener {
    final SimpMessageSendingOperations operations;
    @Override
    public void onPostInsert(PostInsertEvent postInsertEvent) {
        if (postInsertEvent.getEntity() instanceof Message message) {
            for (Member member : message.getChat().getMembers()) {
                operations.convertAndSend("/topic/update/"+member.getUser().getId(), Update.builder()
                                .data(message)
                                .user(member.getUser())
                        .build());
            }
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return true;
    }
}