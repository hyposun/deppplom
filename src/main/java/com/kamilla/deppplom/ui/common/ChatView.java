package com.kamilla.deppplom.ui.common;

import com.kamilla.deppplom.chat.ChatService;
import com.kamilla.deppplom.chat.model.Chat;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UIScope
@SpringComponent
@Route(value = "chats/:userId", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT", "TEACHER"})
public class ChatView extends VerticalLayout implements BeforeEnterObserver {

    private UserService userService;
    private SecurityService securityService;
    private ChatService chatService;

    private User toUser;
    private int fromUserId;

    private H3 header = new H3();
    private MessageList messageList = new MessageList();
    private MessageInput messageInput = new MessageInput();

    public ChatView(
            UserService userService,
            SecurityService securityService,
            ChatService chatService
    ) {
        this.userService = userService;
        this.securityService = securityService;
        this.chatService = chatService;

        messageList.setWidthFull();
        messageList.setHeightFull();
        messageInput.setHeightFull();
        messageInput.setWidthFull();
        messageInput.setI18n(new MessageInputI18n()
                .setMessage("Введите сообщение")
                .setSend("Отправить"));

        add(header, messageList, messageInput);

        messageInput.addSubmitListener(event -> {
            String message = event.getValue();
            if (message == null || message.isBlank()) return;
            chatService.sendMessage(fromUserId, toUser.getId(), message);
            refreshMessages();
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        toUser = event.getRouteParameters().get("userId")
                .map(NumberUtils::toInt)
                .map(it -> userService.findById(it).orElseThrow())
                .orElseThrow();

        fromUserId = securityService.getAuthenticatedUser().orElseThrow().getId();
        header.setText("Чат: " + ChatsView.getUserTitle(toUser));
        refreshMessages();

        findChat().ifPresent(chat -> {
            chatService.markMessagesDelivered(chat.getId(), fromUserId);
        });

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
        return chatService.findChatBetween(fromUserId, toUser.getId());
    }

}
