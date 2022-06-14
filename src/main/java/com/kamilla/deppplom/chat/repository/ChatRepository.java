package com.kamilla.deppplom.chat.repository;

import com.kamilla.deppplom.chat.repository.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    List<ChatEntity> findAllByUserIdsContains(int userId);

}
