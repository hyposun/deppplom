package com.kamilla.deppplom.chat.model;

import com.kamilla.deppplom.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Chat {

    private long id;
    private List<User> participants;
    private List<ChatMessage> messages;
    private boolean updated;

    public User getOppositeUser(int userId) {
        return participants.stream()
                .filter(it -> it.getId() != userId)
                .findFirst().orElseThrow();
    }

    public boolean isUpdaterFor(int userId) {
        return messages.stream()
                .anyMatch(it -> !it.isDelivered() && it.getFrom().getId() != userId);

    }

}

