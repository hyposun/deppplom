package com.kamilla.deppplom.ui.common;

import com.kamilla.deppplom.chat.ChatService;
import com.kamilla.deppplom.chat.model.Chat;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("Deppplom | Сообщения")
@Route(value = "chats/:userId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT", "TEACHER"})
public class ChatView extends VerticalLayout implements BeforeEnterObserver {

    private SecurityService securityService;
    private ChatService chatService;

    private int toUserId;
    private int fromUserId;

    private MessageList messageList = new MessageList();
    private MessageInput messageInput = new MessageInput();

    public ChatView(
            SecurityService securityService,
            ChatService chatService
    ) {
        this.securityService = securityService;
        this.chatService = chatService;

        add(messageList, messageInput);

        messageInput.addSubmitListener(event -> {
            String message = event.getValue();
            if (message == null || message.isBlank()) return;
            chatService.sendMessage(fromUserId, toUserId, message);
            refreshMessages();
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        toUserId = event.getRouteParameters().get("userId").map(NumberUtils::toInt).orElseThrow();
        fromUserId = securityService.getAuthenticatedUser().orElseThrow().getId();
        refreshMessages();
    }

    private void refreshMessages() {
        var maybeChat = findChat();
        if (maybeChat.isEmpty()) return;

        List<MessageListItem> messages = maybeChat.get().getMessages().stream()
                .map(it -> {
                    MessageListItem message = new MessageListItem();
                    message.setText(it.getMessage());
                    message.setTime(it.getTime().toInstant(ZoneOffset.UTC));
                    message.setUserName(it.getFrom().getName());

                    return message;
                })
                .collect(Collectors.toList());
        messageList.setItems(messages);
    }

    private Optional<Chat> findChat() {
        return chatService.findChatBetween(fromUserId, toUserId);
    }

}
