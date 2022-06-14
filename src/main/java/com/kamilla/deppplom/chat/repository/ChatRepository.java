package com.kamilla.deppplom.chat.repository;

import com.kamilla.deppplom.chat.repository.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    List<ChatEntity> findAllByUserIdsContains(int userId);

    Optional<ChatEntity> findAllByUserIdsIn(List<Integer> userIds);

}