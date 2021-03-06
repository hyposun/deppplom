package com.kamilla.deppplom.chat;

import com.kamilla.deppplom.chat.model.Chat;
import com.kamilla.deppplom.chat.model.ChatMessage;
import com.kamilla.deppplom.chat.repository.ChatMessageRepository;
import com.kamilla.deppplom.chat.repository.ChatRepository;
import com.kamilla.deppplom.chat.repository.model.ChatEntity;
import com.kamilla.deppplom.chat.repository.model.ChatMessageEntity;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserService userService;

    public Chat findOrCreateChatBetween(int firstUserId, int secondUserId) {
        return findChatBetween(firstUserId, secondUserId)
                .orElseGet(() -> {
                    User first = userService.findById(firstUserId).orElseThrow();
                    User second = userService.findById(secondUserId).orElseThrow();
                    var participants = List.of(first, second);
                    return new Chat(0, participants, emptyList(), false);
                });
    }

    public Optional<Chat> findChatBetween(int firstUserId, int secondUserId) {
        return chatRepository
                .findAllByUserIdsContains(firstUserId).stream()
                .filter(it -> it.getUserIds().contains(secondUserId))
                .findFirst()
                .map(this::fromEntity);
    }

    public List<Chat> findAllChats(int userId) {
        return chatRepository.findAllByUserIdsContains(userId).stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    public ChatMessage sendMessage(int fromUserId, int toUserId, String message) {
        var chat = findOrCreateEntityChatBetween(fromUserId, toUserId);

        ChatMessageEntity chatMessage = new ChatMessageEntity();
        chatMessage.setChatId(chat.getId());
        chatMessage.setMessage(message);
        chatMessage.setDelivered(false);
        chatMessage.setTime(LocalDateTime.now());
        chatMessage.setUserId(fromUserId);

        chatMessage = chatMessageRepository.save(chatMessage);
        return fromEntity(chatMessage);
    }

    public void markMessagesDelivered(long chatId, int userId) {
        var chat = chatRepository.findById(chatId).orElseThrow();
        chatMessageRepository
                .findAllByChatId(chat.getId()).stream()
                .filter(it -> !it.isDelivered() && it.getUserId() != userId)
                .forEach(message -> {
                    message.setDelivered(true);
                    chatMessageRepository.save(message);
                });
    }

    private ChatEntity findOrCreateEntityChatBetween(int fromUserId, int toUserId) {
        var participants = List.of(fromUserId, toUserId);
        return chatRepository.findAllByUserIdsContains(fromUserId).stream()
                .filter(it -> it.getUserIds().contains(toUserId))
                .findFirst()
                .orElseGet(() -> chatRepository.save(new ChatEntity(0, participants)));
    }

    private Chat fromEntity(ChatEntity chat) {
        List<ChatMessage> messages = chatMessageRepository
                .findAllByChatId(chat.getId()).stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
        boolean updated = messages.stream().anyMatch(message -> !message.isDelivered());
        List<User> participants = chat.getUserIds().stream()
                .map(it -> userService.findById(it).orElseThrow())
                .collect(Collectors.toList());
        return new Chat(chat.getId(), participants, messages, updated);
    }

    private ChatMessage fromEntity(ChatMessageEntity message) {
        var user = userService.findById(message.getUserId()).orElseThrow();
        return new ChatMessage(
            message.getId(),
            user,
            message.getMessage(),
            message.getTime(),
            message.isDelivered()
        );
    }

}

