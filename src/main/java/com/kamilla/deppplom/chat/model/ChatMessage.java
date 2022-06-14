package com.kamilla.deppplom.chat.model;

import com.kamilla.deppplom.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessage {
    private long id;
    private User from;
    private String message;
    private LocalDateTime time;
    private boolean delivered;
}
